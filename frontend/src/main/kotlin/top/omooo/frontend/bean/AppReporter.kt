package top.omooo.frontend.bean

/**
 * Author: Omooo
 * Date: 2023/2/3
 * Desc: App 报告类
 */
@kotlinx.serialization.Serializable
data class AppReporter(
    /** 描述信息 */
    val desc: String,
    /** 文档链接 */
    val documentLink: String,
    /** 版本号 */
    val versionName: String,
    /** 构建类型名 */
    val variantName: String,
    /** AAR 列表 */
    val aarList: List<AarFile>,
)