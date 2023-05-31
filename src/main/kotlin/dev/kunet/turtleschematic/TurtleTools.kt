package dev.kunet.turtleschematic

import dev.kunet.turtleschematic.nms.TurtleChunk
import dev.kunet.turtleschematic.nms.TurtleWorld
import dev.kunet.turtleschematic.nms.constructChunkSection
import org.bukkit.World

object TurtleTools {
    fun fastClearChunk(world: World, x: Int, z: Int) {
        if (!world.isChunkLoaded(x, z)) world.loadChunk(x, z)
        val nmsWorldContext = TurtleWorld(world)
        val chunk = TurtleChunk(nmsWorldContext.chunkHandleAt(x, z))
        val skyLight = world.environment == World.Environment.NORMAL
        val sections = Array<Any>(16) { constructChunkSection(it shr 4 shl 4, skyLight) }

        sections.copyInto(chunk.getSections())

        nmsWorldContext.refreshChunk(x, z)
        nmsWorldContext.updateLightingChunk(x, z)
    }
}
