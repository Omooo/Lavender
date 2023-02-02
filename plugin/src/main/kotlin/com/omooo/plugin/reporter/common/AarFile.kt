package com.omooo.plugin.reporter.common

/**
 * Author: Omooo
 * Date: 2023/2/2
 * Desc:
 */
internal data class AarFile(
    val name: String,
    val size: Long,
    val owner: String,
    val fileList: List<AppFile>,
)