package com.omooo.plugin.util

import org.objectweb.asm.tree.MethodInsnNode

/**
 * [MethodInsnNode] 转文本
 */
internal fun MethodInsnNode.toPlainText(): String {
    return "$owner#$name$desc"
}