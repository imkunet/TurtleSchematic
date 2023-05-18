package dev.kunet.turtleschematic

import dev.kunet.turtleschematic.nms.initializeBlockHandles

object TurtleLibrary {
    private var initialized = false
    fun initializeTurtle() {
        if (initialized) return
        initialized = true

        initializeBlockHandles()
    }
}