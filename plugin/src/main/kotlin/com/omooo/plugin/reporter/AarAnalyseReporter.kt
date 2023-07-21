package com.omooo.plugin.reporter

import com.omooo.plugin.reporter.common.AarFile

/**
 * Author: Omooo
 * Date: 2023/7/14
 * Desc: AAR 分析报告
 */
@kotlinx.serialization.Serializable
internal data class AarAnalyseReporter(
    /** 描述信息 */
    val desc: String,
    /** 文档链接 */
    val documentLink: String,
    /** 当前版本号 */
    val currentVersionName: String,
    /** 上个版本号 */
    val previousVersionName: String,
    /** 当前版本 AAR 列表 */
    val currentList: List<AarFile>,
    /** 上个版本 AAR 列表 */
    val previousList: List<AarFile>,
)
