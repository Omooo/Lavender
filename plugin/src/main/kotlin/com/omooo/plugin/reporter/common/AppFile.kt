package com.omooo.plugin.reporter.common

/**
 * Author: Omooo
 * Date: 2023/2/2
 * Desc: 表示一个 Apk 里的文件
 */

@kotlinx.serialization.Serializable
internal data class AppFile(
    var name: String,
    val size: Long = 0,
    val desc: String = "",
    var fileType: FileType = FileType.OTHER,
)

internal fun List<AppFile>.totalSize(): Long {
    return map { it.size }.reduce { acc, l -> acc + l }
}

