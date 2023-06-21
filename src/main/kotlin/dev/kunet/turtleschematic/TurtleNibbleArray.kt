package dev.kunet.turtleschematic

import dev.kunet.turtleschematic.nms.getterNibbleInternalArray

class TurtleNibbleArray(val handle: Any) {
    fun getArray() = getterNibbleInternalArray(handle)

}
