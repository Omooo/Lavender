package com.omooo.plugin.reporter.common

/**
 * Author: Omooo
 * Date: 2023/2/2
 * Desc: 表示一个 Apk 里的文件
 */

@kotlinx.serialization.Serializable
internal data class AppFile(
    var name: String,
    var size: Long = 0,
    var desc: String = "",
    var fileType: FileType = FileType.OTHER,
)

internal fun List<AppFile>.totalSize(): Long {
    if (isEmpty()) {
        return 0
    }
    return map { it.size }.reduce { acc, l -> acc + l }
}

