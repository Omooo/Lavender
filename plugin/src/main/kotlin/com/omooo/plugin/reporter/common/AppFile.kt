package com.omooo.plugin.reporter.common

/**
 * Author: Omooo
 * Date: 2023/2/2
 * Desc:
 */

@kotlinx.serialization.Serializable
internal data class AppFile(
    val name: String,
    val size: Long,
)

