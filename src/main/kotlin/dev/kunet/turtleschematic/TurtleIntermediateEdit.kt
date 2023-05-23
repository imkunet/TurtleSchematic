package dev.kunet.turtleschematic

import dev.kunet.turtleschematic.nms.TurtleChunk
import dev.kunet.turtleschematic.nms.TurtleWorld
import org.bukkit.World
import kotlin.system.measureTimeMillis

class TurtleIntermediateEdit(private val world: World, private val intermediary: TurtleIntermediate) {
    private var finishedPlacing = false
    private val nmsWorldContext = TurtleWorld(world)

    private var chunkHandleMap = hashMapOf<Long, TurtleChunk>()
    private fun getChunkHandle(x: Int, z: Int): TurtleChunk {
        val hash = packXZ(x, z)
        return chunkHandleMap.getOrPut(hash) {
            if (!world.isChunkLoaded(x, z)) world.loadChunk(x, z)
            TurtleChunk(nmsWorldContext.chunkHandleAt(x, z))
        }
    }

    private var dataChunkIndex = 0
    fun poll(timeLimit: Int = -1): Boolean {
        var timeSum = 0L
        var meanTime = 0L

        val keys = intermediary.intermediateChunks.keys

        prison@ while (true) {
            val t = measureTimeMillis {
                if (dataChunkIndex >= keys.size) finishedPlacing = true
                if (finishedPlacing) return@measureTimeMillis

                val hash = keys.elementAt(dataChunkIndex)
                val cx = unpackX(hash)
                val cz = unpackZ(hash)
                val handle = getChunkHandle(cx, cz)

                intermediary.intermediateChunks[hash]?.sections?.map { it.section }?.toTypedArray()
                    ?.copyInto(handle.getSections())
                nmsWorldContext.refreshChunk(cx, cz)
            }

            timeSum += t
            meanTime += t
            meanTime /= 2
            dataChunkIndex++

            // if it's finished, done! if it's exceeded computation time, done!
            if (finishedPlacing || (timeLimit > 1 && timeSum + meanTime > timeLimit)) break@prison
        }

        return finishedPlacing
    }
}