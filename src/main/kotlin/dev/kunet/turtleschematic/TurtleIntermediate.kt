package dev.kunet.turtleschematic

import dev.kunet.turtleschematic.nms.TurtleChunkSection
import kotlinx.coroutines.sync.withLock

class TurtleIntermediate {
    internal var initialized = false
    internal var intermediateChunks = hashMapOf<Long, TurtleIntermediateChunk>()

    private fun getIntermediateChunk(x: Int, z: Int): TurtleIntermediateChunk {
        val hash = packXZ(x, z)
        return intermediateChunks.getOrPut(hash) {
            TurtleIntermediateChunk()
        }
    }

    class TurtleIntermediateChunk {
        // TODO: don't assume the thing is in the normal world :)

        // note the << 4 >> 4 is a hack to get the block y pos of the section
        val sections = Array(16) { TurtleChunkSection(it shr 4 shl 4) }

        fun getSection(y: Int) = sections[y shr 4]
    }

    internal suspend fun initializeWithData(parent: TurtleSchematic, offsetX: Int, offsetY: Int, offsetZ: Int) =
        parent.mutex.withLock {
            var previousChunkX = offsetX shr 4
            var previousChunkZ = offsetZ shr 4
            var previousChunk = getIntermediateChunk(previousChunkX, previousChunkZ)

            var previousSectionY = 0
            var previousSection = previousChunk.sections[0]

            println("wlh ${parent.width}x${parent.length}x${parent.height} (${parent.width * parent.length * parent.height})")

            for (l in 0 until parent.length) {
                for (w in 0 until parent.width) {
                    for (h in 0 until parent.height) {
                        // TODO: see if not random seeking the HUGE array makes it any faster
                        val i = parent.getBlockIndex(w, h, l)
                        val blockId = parent.blocks[i].toInt() and 0x00FF

                        if (blockId == 0) continue

                        val x = w + offsetX
                        val y = h + offsetY
                        val z = l + offsetZ

                        val currentChunkX = x shr 4
                        val currentChunkZ = z shr 4

                        var currentChunk = previousChunk

                        if (currentChunkX != previousChunkX || currentChunkZ != previousChunkZ) {
                            previousChunkX = currentChunkX
                            previousChunkZ = currentChunkZ

                            currentChunk = getIntermediateChunk(currentChunkX, currentChunkZ)
                            previousChunk = currentChunk

                            previousSectionY = y shr 4
                            previousSection = currentChunk.getSection(y)
                        }

                        var currentSection = previousSection
                        if (previousSectionY != y shr 4) {
                            previousSectionY = y shr 4
                            currentSection = currentChunk.getSection(y)
                            previousSection = currentSection
                        }

                        currentSection.setBlock(x, y, z, blockId, parent.data[i].toInt())
                    }
                }
            }

            initialized = true
        }
}
