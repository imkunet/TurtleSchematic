package dev.kunet.turtleschematic

fun now() = System.currentTimeMillis()

fun packXZ(x: Int, z: Int): Long = (x.toLong() shl 32) or (z.toLong() and 0xFFFFFFFF)

fun unpackX(packed: Long) = (packed shr 32).toInt()
fun unpackZ(packed: Long) = (packed and 0xFFFFFFFF).toInt()
