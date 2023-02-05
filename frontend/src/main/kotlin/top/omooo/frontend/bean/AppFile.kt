package top.omooo.frontend.bean

import kotlinx.serialization.Serializable

/**
 * Author: Omooo
 * Date: 2023/2/2
 * Desc:
 */
@Serializable
data class AppFile(
    val name: String,
    val size: Long,
)

