package top.omooo.frontend.bean

/**
 * Author: Omooo
 * Date: 2023/7/14
 * Desc: AAR 分析报告
 */
@kotlinx.serialization.Serializable
data class AarAnalyseReporter(
    /** 描述信息 */
    val desc: String,
    /** 文档链接 */
    val documentLink: String,
    /** 包名 */
    val packageName: String,
    /** 版本号 */
    val versionName: String,
    /** 当前版本 AAR 列表 */
    val currentList: List<AarFile>,
    /** 之前版本 AAR 列表 */
    val previousList: List<Pair<String, List<AarFile>>>,
    /** 所属人映射 */
    val ownerMap: Map<String, List<String>> = emptyMap(),
)