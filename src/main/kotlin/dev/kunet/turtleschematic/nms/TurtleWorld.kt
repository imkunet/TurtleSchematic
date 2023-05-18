package dev.kunet.turtleschematic.nms

import org.bukkit.World

class TurtleWorld(private val world: World) {
    private val worldHandle: Any = getHandle(world)

    fun chunkHandleAt(chunkX: Int, chunkZ: Int): Any {
        return chunkHandleAt(worldHandle, chunkX, chunkZ)
    }

    fun refreshChunk(x: Int, z: Int) {
        craftRefreshChunk(world, x, z)
    }

    fun updateBlockAt(x: Int, y: Int, z: Int) {
        notify(worldHandle, x, y, z)
    }
}
