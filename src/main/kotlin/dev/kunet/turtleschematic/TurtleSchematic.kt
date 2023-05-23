package dev.kunet.turtleschematic

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.querz.nbt.io.NBTUtil
import net.querz.nbt.tag.CompoundTag
import java.io.File
import java.io.IOException
import java.lang.Exception

class TurtleSchematic(
    private val file: File,
) {
    internal val mutex: Mutex = Mutex()

    init {
        //runBlocking { if (!startReading()) throw IOException("wtf") }
    }

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

    var worldEditX: Int? = null
    var worldEditY: Int? = null
    var worldEditZ: Int? = null

    fun getBlockIndex(x: Int, y: Int, z: Int) = (y * length + z) * width + x

    fun totalBlocks() = width * length * height

    // blocking and slow function to create the interemediary
    fun createIntermediary(offsetX: Int, offsetY: Int, offsetZ: Int): TurtleIntermediate = runBlocking {
        val it = TurtleIntermediate()
        it.initializeWithData(this@TurtleSchematic, offsetX, offsetY, offsetZ)
        return@runBlocking it
    }

    fun createIntermediaryFromWEOrigin(x: Int, y: Int, z: Int): TurtleIntermediate {
        return createIntermediary(worldEditX?.plus(x) ?: x, worldEditY?.plus(y) ?: y, worldEditZ?.plus(z) ?: -z)
    }

    fun startReading(): Boolean = runBlocking {
        mutex.withLock {
            val compound: CompoundTag?
            try {
                val start = now()
                println("Reading schematic $file")
                compound = NBTUtil.read(file).tag as CompoundTag?
                println("Finished reading $file in ${now() - start}ms")
            } catch (exception: IOException) {
                exception.printStackTrace()
                return@runBlocking false
            }

            if (compound == null) {
                return@runBlocking false
            }

            width = compound.getShort("Width").toInt()
            length = compound.getShort("Length").toInt()
            height = compound.getShort("Height").toInt()

            blocks = compound.getByteArray("Blocks")
            data = compound.getByteArray("Data")

            try {
                worldEditX = compound.getInt("WEOffsetX")
                worldEditY = compound.getInt("WEOffsetY")
                worldEditZ = compound.getInt("WEOffsetZ")
                println("WE Offset data: ($worldEditX, $worldEditY, $worldEditZ)")
            } catch (throwable: Exception) {
                // nope!
            }

            return@runBlocking true
        }
    }
}
