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
    return substringBeforeLast(":").substringAfter(":")
}