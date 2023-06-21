package dev.kunet.turtleschematic.nms

import dev.kunet.turtleschematic.TURTLE_DEBUG

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
internal val nmsRegistryId = getNMSClass("RegistryID")
internal val nmsBlockPosition = getNMSClass("BlockPosition")
internal val nmsNibbleArray = getNMSClass("NibbleArray")

internal val constructChunkSection = getConstructor(nmsChunkSection, int, boolean)
internal val constructBlockPosition = getConstructor(nmsBlockPosition, int, int, int)

internal val invokeGetByCombinedId = getStaticMethod(nmsBlock, "getByCombinedId", nmsIBlockData, int)

internal val invokeGetHandle = getMethod(craftWorld, "getHandle", nmsWorldServer)
internal val invokeRefreshChunk = getMethod(craftWorld, "refreshChunk", boolean, int, int)
internal val invokeGetChunk = getMethod(nmsWorld, "getChunkAt", nmsChunk, int, int)
internal val invokeUpdateLighting = getMethod(nmsWorld, "x", boolean, nmsBlockPosition)
internal val invokeChunkUpdateLighting = getMethod(nmsChunk, "n", void)
internal val invokeGetRegistryId = getMethod(nmsRegistryId, "b", int, Any::class.java)

internal val getterChunkSections = getPrivateField(nmsChunk, "sections")
internal val getterBlockIds = getPrivateField(nmsChunkSection, "blockIds")
internal val getterSkylightNibbleArray = getPrivateField(nmsChunkSection, "skyLight")
internal val getterNibbleInternalArray = getPrivateField(nmsNibbleArray, "a")

internal val setNonEmptyBlockCount = setPrivateField(nmsChunkSection, "nonEmptyBlockCount")
internal val setTickingBlockCount = setPrivateField(nmsChunkSection, "tickingBlockCount")

internal val staticBlockRegistry = getStaticField(nmsBlock, "d", nmsRegistryId)()

internal fun getIBlockData(id: Int, data: Int) = invokeGetByCombinedId(id + (data shl 12))

@Suppress("UNCHECKED_CAST")
internal fun getChunkSections(chunk: Any) = getterChunkSections(chunk) as Array<Any>

internal fun chunkSectionGetBlockIdArray(chunkSection: Any) = getterBlockIds(chunkSection) as CharArray

fun initializeBlockHandles() {
    if (TURTLE_DEBUG) println("Initializing block handles...")
}
