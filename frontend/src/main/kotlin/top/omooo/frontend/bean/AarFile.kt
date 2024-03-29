package top.omooo.frontend.bean

import kotlinx.serialization.Serializable

/**
 * Author: Omooo
 * Date: 2023/2/3
 * Desc:
 */
@Serializable
data class AarFile(
    val name: String,
    val size: Long,
    val owner: String,
    val fileList: List<AppFile> = emptyList(),
)

internal fun List<AarFile>.totalSize(): Long {
    if (isEmpty()) {
        return 0
    }
    return map { it.size }.reduce { acc, l -> acc + l }
}