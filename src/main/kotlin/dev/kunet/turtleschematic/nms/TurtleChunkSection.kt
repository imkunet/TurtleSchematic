package dev.kunet.turtleschematic.nms

class TurtleChunkSection(y: Int) {
    val section = createChunkSection(y, true)

    // TODO: even faster by skipping block LUT, obj insn, and shortcutting chunk block count tracking
    fun setBlock(x: Int, y: Int, z: Int, id: Int, data: Int) {
        chunkSectionSetType(section, x and 15, y and 15, z and 15, getIBlockData(id, data))
    }
}