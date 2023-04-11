package com.omooo.plugin.reporter.common

/**
 * Author: Omooo
 * Date: 2023/2/2
 * Desc:
 */
@kotlinx.serialization.Serializable
internal data class AarFile(
    val name: String,
    var size: Long,
    val owner: String,
    val fileList: MutableList<AppFile>,
)

internal fun List<AarFile>.totalSize(): Long {
    if (isEmpty()) {
        return 0
    }
    return map { it.size }.reduce { acc, l -> acc + l }
}