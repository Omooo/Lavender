package com.omooo.plugin.util

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * Author: Omooo
 * Date: 2022/12/28
 * Desc: ClassNode 扩展方法
 */

val ClassNode.className: String
    get() = name.replace('/', '.')

val ClassNode.isAnnotation: Boolean
    get() = 0 != (access and Opcodes.ACC_ANNOTATION)

val ClassNode.isInterface: Boolean
    get() = 0 != (access and Opcodes.ACC_INTERFACE)

val ClassNode.isAbstract: Boolean
    get() = 0 != (access and Opcodes.ACC_ABSTRACT)

val ClassNode.isPublic: Boolean
    get() = 0 != (access and Opcodes.ACC_PUBLIC)

val ClassNode.isProtected: Boolean
    get() = 0 != (access and Opcodes.ACC_PROTECTED)

val ClassNode.isPrivate: Boolean
    get() = 0 != (access and Opcodes.ACC_PRIVATE)

val ClassNode.isStatic: Boolean
    get() = 0 != (access and Opcodes.ACC_STATIC)

val ClassNode.isFinal: Boolean
    get() = 0 != (access and Opcodes.ACC_FINAL)