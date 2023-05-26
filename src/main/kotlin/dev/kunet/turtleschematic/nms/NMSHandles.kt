package dev.kunet.turtleschematic.nms

private val void = Void::class.javaPrimitiveType!!
private val boolean = Boolean::class.javaPrimitiveType!!
private val int = Int::class.javaPrimitiveType!!

internal val craftWorld = getBukkitClass("CraftWorld")
internal val nmsWorldServer = getNMSClass("WorldServer")
internal val nmsWorld = getNMSClass("World")
internal val nmsChunk = getNMSClass("Chunk")
internal val nmsBlock = getNMSClass("Block")
internal val nmsIBlockData = getNMSClass("IBlockData")
internal val nmsChunkSection = getNMSClass("ChunkSection")
internal val nmsBlocks = getNMSClass("Blocks")
internal val nmsBlockPosition = getNMSClass("BlockPosition")

internal val chunkSectionConstructor = getConstructor(nmsChunkSection, int, boolean)
internal val mutableBlockPositionConstructor = getConstructor(nmsBlockPosition, int, int, int)

internal val getByCombinedId = getStaticMethod(nmsBlock, "getByCombinedId", nmsIBlockData, int)

internal val getHandle = getMethod(craftWorld, "getHandle", nmsWorldServer)
internal val getChunk = getMethod(nmsWorld, "getChunkAt", nmsChunk, int, int)
internal val craftRefreshChunk = getMethod(craftWorld, "refreshChunk", boolean, int, int)
internal val getType = getMethod(nmsChunkSection, "getType", nmsIBlockData, int, int, int)
internal val setType = getMethod(nmsChunkSection, "setType", void, int, int, int, nmsIBlockData)
internal val blockGetBlockData = getMethod(nmsBlock, "getBlockData", nmsIBlockData)
internal val notify = getMethod(nmsWorld, "notify", void, nmsBlockPosition)
internal val x = getMethod(nmsWorld, "x", boolean, nmsBlockPosition)

internal val chunkSections = getPrivateField(nmsChunk, "sections")
internal val AIR = getStaticField(nmsBlocks, "AIR", nmsBlock)

internal val airBlockData = blockGetBlockData(AIR())

internal fun getIBlockData(id: Int, data: Int) = getByCombinedId(id + (data shl 12))

@Suppress("UNCHECKED_CAST")
internal fun getChunkSections(chunk: Any) = chunkSections(chunk) as Array<Any>
internal fun createChunkSection(y: Int, skyLight: Boolean) = chunkSectionConstructor(y, skyLight)
internal fun chunkSectionGetType(chunkSection: Any, x: Int, y: Int, z: Int) = getType(chunkSection, x, y, z)
internal fun chunkSectionSetType(chunkSection: Any, x: Int, y: Int, z: Int, blockData: Any) =
    setType(chunkSection, x, y, z, blockData)

internal fun chunkHandleAt(worldHandle: Any, x: Int, z: Int): Any {
    return getChunk(worldHandle, x, z)
}

internal fun getBlockData(chunkSection: Any?, x: Int, y: Int, z: Int): Any {
    if (chunkSection == null) return airBlockData
    return chunkSectionGetType(chunkSection, x and 15, y and 15, z and 15) ?: airBlockData
}

internal fun setBlockData(chunkSection: Any?, x: Int, y: Int, z: Int, blockData: Any) {
    if (chunkSection == null) return
    chunkSectionSetType(chunkSection, x and 15, y and 15, z and 15, blockData)
}

internal fun createBlockPositionHandle(x: Int, y: Int, z: Int) = mutableBlockPositionConstructor(x, y, z)

internal fun notify(worldHandle: Any, x: Int, y: Int, z: Int) {
    notify(worldHandle, createBlockPositionHandle(x, y, z))
}

internal val TURTLE_DEBUG = System.getenv("TURTLE_DEBUG").equals("true", true)

fun initializeBlockHandles() {
    if (TURTLE_DEBUG) println("Initializing block handles...")
}
