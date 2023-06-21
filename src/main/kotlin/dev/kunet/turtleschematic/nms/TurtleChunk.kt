package dev.kunet.turtleschematic.nms

class TurtleChunk(val handle: Any) {
    fun getSections() = getChunkSections(handle)
    fun refreshLighting() = invokeChunkUpdateLighting(handle)
}
