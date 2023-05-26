package dev.kunet.turtleschematic

import dev.kunet.turtleschematic.nms.*
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap
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
        // note the << 4 >> 4 is a hack to get the block y pos of the section
        val sections = Array(16) { TurtleIntermediateChunkSection(it shr 4 shl 4) }

        fun getSection(y: Int) = sections[y shr 4]
    }

    class TurtleIntermediateChunkSection(val yPos: Int) {
        private var tickingBlockCounter = 0
        private fun nonTickingBlockCount() = 4096 - tickingBlockCounter

        private var blockIds = CharArray(4096)

        fun setBlock(x: Int, y: Int, z: Int, c: Int) {
            blockIds[(y and 15 shl 8) or (z and 15 shl 4) or (x and 15)] = c.toChar()
            // assuming all set block aren't air and positions don't conflict
            tickingBlockCounter++
        }

        fun emitChunkSection(skyLight: Boolean): Any {
            val chunkSection = constructChunkSection(yPos, skyLight)
            setNonEmptyBlockCount(chunkSection, nonTickingBlockCount())
            setTickingBlockCount(chunkSection, tickingBlockCounter)
            blockIds.copyInto(chunkSectionGetBlockIdArray(chunkSection))

            return chunkSection
        }
    }

    internal suspend fun initializeWithData(parent: TurtleSchematic, offsetX: Int, offsetY: Int, offsetZ: Int) =
        parent.mutex.withLock {
            var previousChunkX = offsetX shr 4
            var previousChunkZ = offsetZ shr 4
            var previousChunk = getIntermediateChunk(previousChunkX, previousChunkZ)

            var previousSectionY = 0
            var previousSection = previousChunk.sections[0]

            val blockCache = Int2IntArrayMap(16)

            if (TURTLE_DEBUG) println("Dimensions (WxLxH): ${parent.width}x${parent.length}x${parent.height} (Volume: ${parent.width * parent.length * parent.height})")

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

                        val blockData = parent.data[i].toInt()
                        val blockKey = blockId + (blockData shl 12)
                        var block = blockCache.getOrDefault(blockKey, Int.MIN_VALUE)
                        if (block == Int.MIN_VALUE) {
                            block = invokeGetRegistryId(staticBlockRegistry, getIBlockData(blockId, blockData)) as Int
                            blockCache.put(blockKey, block)
                        }

                        currentSection.setBlock(x, y, z, block)
                    }
                }
            }

            initialized = true
        }
}
