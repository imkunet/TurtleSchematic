package dev.kunet.turtleschematic

import dev.kunet.turtleschematic.nms.*
import dev.kunet.turtleschematic.nms.getIBlockData
import dev.kunet.turtleschematic.nms.invokeGetRegistryId
import dev.kunet.turtleschematic.nms.staticBlockRegistry
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap

class TurtleView(val bukkitWorld: org.bukkit.World) {
    private val turtleWorld = TurtleWorld(bukkitWorld)
    private val blockCache = Int2IntArrayMap(16)

    private fun getChunkHandle(x: Int, z: Int): TurtleChunk {
        if (!bukkitWorld.isChunkLoaded(x, z)) bukkitWorld.loadChunk(x, z)
        return TurtleChunk(turtleWorld.chunkHandleAt(x, z))
    }

    fun setBlock(x: Int, y: Int, z: Int, blockId: Int, blockData: Int) {
        val blockKey = blockId + (blockData shl 12)
        var block = blockCache.getOrDefault(blockKey, Int.MIN_VALUE)
        if (block == Int.MIN_VALUE) {
            block = invokeGetRegistryId(staticBlockRegistry, getIBlockData(blockId, blockData)) as Int
            blockCache.put(blockKey, block)
        }

        val chunk = getChunkHandle(x shr 4, z shr 4)
        val section = chunk.getSections()[y shr 4]

        val blockIds = chunkSectionGetBlockIdArray(section)
        blockIds[(y and 15 shl 8) or (z and 15 shl 4) or (x and 15)] = block.toChar()

        turtleWorld.notify(x, y, z)
    }
}
