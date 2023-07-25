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
    /** 包名 */
    var packageName: String,
    /** AAR 列表 */
    var aarList: ArrayList<Pair<String, List<AarFile>>>,
    /** 所属人映射 */
    var ownerMap: Map<String, List<String>> = emptyMap(),
)
