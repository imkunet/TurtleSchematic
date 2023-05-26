package dev.kunet.turtleschematic

import dev.kunet.turtleschematic.nms.initializeBlockHandles

internal val TURTLE_DEBUG = System.getenv("TURTLE_DEBUG").equals("true", true)

object TurtleLibrary {
    private var initialized = false
    fun initializeTurtle() {
        if (initialized) return
        initialized = true

        initializeBlockHandles()
    }
}