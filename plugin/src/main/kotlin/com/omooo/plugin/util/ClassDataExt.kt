package com.omooo.plugin.util

import com.android.build.api.instrumentation.ClassData

/**
 * 是否是 R 文件
 *
 * @return true: R 文件
 */
internal fun ClassData.isRClass(): Boolean {
    return className.split(".").lastOrNull()?.let {
        it == "R" || it.startsWith("R$")
    } ?: false
}

/**
 * 是否是系统类
 *
 * @return true: 系统类
 */
internal fun ClassData.isSystemClass(): Boolean {
    val filterList = arrayOf(
        "kotlin.", "org.intellij.", "androidx.",
        "com.google.", "org.jetbrains.", "android.",
    )
    return filterList.find {
        className.startsWith(it)
    } != null
}