package com.omooo.plugin.util

/**
 * Author: Omooo
 * Date: 2023/2/4
 * Desc: String 相关扩展函数
 */

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