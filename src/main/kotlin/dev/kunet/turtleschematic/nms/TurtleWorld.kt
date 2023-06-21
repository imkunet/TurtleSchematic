package dev.kunet.turtleschematic.nms

import org.bukkit.World

class TurtleWorld(private val world: World) {
    private val worldHandle: Any = invokeGetHandle(world)

    fun chunkHandleAt(chunkX: Int, chunkZ: Int): Any {
        return invokeGetChunk(worldHandle, chunkX, chunkZ)
    }

    fun refreshChunk(x: Int, z: Int) {
        invokeRefreshChunk(world, x, z)
    }

    fun updateLighting(x: Int, y: Int, z: Int) {
        invokeUpdateLighting(worldHandle, constructBlockPosition(x, y, z))
    }
}
