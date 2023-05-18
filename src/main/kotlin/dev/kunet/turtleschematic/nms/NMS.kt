package dev.kunet.turtleschematic.nms

import org.bukkit.Bukkit
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

val NMS_VERSION = Bukkit.getServer().javaClass.`package`.name.split(".").last()

fun getNMSClass(name: String): Class<*> {
    return Class.forName("net.minecraft.server.$NMS_VERSION.$name")
}

fun getBukkitClass(name: String): Class<*> {
    return Class.forName("org.bukkit.craftbukkit.$NMS_VERSION.$name")
}

private val lookup = MethodHandles.lookup()

fun getMethod(type: Class<*>, method: String, returnType: Class<*>, vararg parameterTypes: Class<*>): MethodHandle {
    return lookup.findVirtual(type, method, MethodType.methodType(returnType, parameterTypes))
}

fun getConstructor(type: Class<*>, vararg parameterTypes: Class<*>): MethodHandle {
    return lookup.findConstructor(type, MethodType.methodType(Void.TYPE, parameterTypes))
}

fun getStaticMethod(
    type: Class<*>,
    method: String,
    returnType: Class<*>,
    vararg parameterTypes: Class<*>
): MethodHandle {
    return lookup.findStatic(type, method, MethodType.methodType(returnType, parameterTypes))
}

fun getField(type: Class<*>, field: String, fieldType: Class<*>): MethodHandle {
    return lookup.findGetter(type, field, fieldType)
}

fun getStaticField(type: Class<*>, field: String, fieldType: Class<*>): MethodHandle {
    return lookup.findStaticGetter(type, field, fieldType)
}

fun getPrivateField(type: Class<*>, field: String): MethodHandle {
    val declaredField = type.getDeclaredField(field)
    declaredField.isAccessible = true

    return lookup.unreflectGetter(declaredField)
}
