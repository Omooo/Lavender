package com.omooo.plugin.internal

/**
 * Author: Omooo
 * Date: 2024/5/7
 * Desc: 产物类型
 */
internal enum class ArtifactType(val type: String, val prefix: String) {
    CLASS("android-classes", ""),
    RES("android-res", "res"),
    ASSETS("android-assets", "assets"),
    NATIVE_LIB("android-jni", "lib"),
}