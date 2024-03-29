package com.omooo.plugin.util

/**
 * Author: Omooo
 * Date: 2023/2/4
 * Desc: String 相关扩展函数
 */

/**
 * 从 AAR 全限定名中获取 group id
 */
internal fun String.getGroupIdFromAarName(): String {
    return substringBefore(":")
}

/**
 * 从 AAR 全限定名中移除版本信息
 *
 * ag: com.google.android.material:material:1.0.0 -> com.google.android.material:material
 */
internal fun String.removeVersionFromAarName(): String {
    return substringBeforeLast(":")
}

/**
 * 从 AAR 全限定名中获取 artifact id
 */
internal fun String.getArtifactIdFromAarName(): String {
    return substringAfter(":").substringBeforeLast(":")
}

/**
 * The following classes exclude from lint
 *
 * - `android.**`
 * - `androidx.**`
 * - `com.android.**`
 * - `com.google.android.**`
 * - `com.google.gson.**`
 * - `**.R`
 * - `**.R$*`
 * - `BuildConfig`
 */
private const val DOLLAR = '$'

internal val EXCLUDES = Regex("^(((android[x]?)|(com/(((google/)?android)|(google/gson))))/.+)|(.+/((R[2]?(${DOLLAR}[a-z]+)?)|(BuildConfig)))$")

internal fun String.formatDollar(): String {
    return replace("$", "${'$'}")
}