package dev.kunet.turtleschematic

fun now() = System.currentTimeMillis()

fun packXZ(x: Int, z: Int): Long = (x.toLong() shl 32) or (z.toLong() and 0xFFFFFFFF)

fun unpackX(packed: Long) = (packed shr 32).toInt()
fun unpackZ(packed: Long) = (packed and 0xFFFFFFFF).toInt()

/*
 * initialize a smaller piece of the array and use the System.arraycopy
 * call to fill in the rest of the array in an expanding binary fashion
 */
fun bytefill(array: ByteArray, value: Byte) {
    val len = array.size
    if (len > 0) {
        array[0] = value
    }

    //Value of i will be [1, 2, 4, 8, 16, 32, ..., len]
    var i = 1
    while (i < len) {
        System.arraycopy(array, 0, array, i, if (len - i < i) len - i else i)
        i += i
    }
}
