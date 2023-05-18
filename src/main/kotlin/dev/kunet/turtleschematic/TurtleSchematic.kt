package dev.kunet.turtleschematic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.querz.nbt.io.NBTUtil
import net.querz.nbt.tag.CompoundTag
import java.io.File
import java.io.IOException
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TurtleSchematic(
    private val file: File,
) {
    internal val lock = ReentrantReadWriteLock()

    var width = 0
        private set

    var length = 0
        private set

    var height = 0
        private set

    var blocks = ByteArray(0)
        private set

    var data = ByteArray(0)
        private set

    fun getBlockIndex(x: Int, y: Int, z: Int) = (y * length + z) * width + x

    fun totalBlocks() = width * length * height

    // blocking and slow function to create the interemediary
    fun createIntermediary(offsetX: Int, offsetY: Int, offsetZ: Int): TurtleIntermediate {
        val it = TurtleIntermediate()
        it.initializeWithData(this, offsetX, offsetY, offsetZ)
        return it
    }

    internal fun startReading(): Boolean {
        lock.write {
            val compound: CompoundTag?
            try {
                println("ata")
                compound = NBTUtil.read(file).tag as CompoundTag?
                println("atb")
            } catch (exception: IOException) {
                exception.printStackTrace()
                lock.writeLock().unlock()
                return false
            }

            if (compound == null) {
                return false
            }

            width = compound.getShort("Width").toInt()
            length = compound.getShort("Length").toInt()
            height = compound.getShort("Height").toInt()

            blocks = compound.getByteArray("Blocks")
            data = compound.getByteArray("Data")
        }

        return true
    }
}

// returns null if there is an error and sits down and shuts up :)
suspend fun createTurtleSchematic(file: File): TurtleSchematic? = withContext(Dispatchers.IO) {
    suspendCoroutine { top ->
        val schematic = TurtleSchematic(file)
        runBlocking {
            try {
                if (!schematic.startReading()) top.resume(null)
                top.resume(schematic)
            } catch (exception: Exception) {
                // absolutely mental!!!
                top.resume(null)
            }
        }
    }
}