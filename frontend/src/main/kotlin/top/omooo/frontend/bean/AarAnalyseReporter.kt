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
    /** AAR 列表 */
    val aarList: List<Pair<String, List<AarFile>>>,
    /** 所属人映射 */
    val ownerMap: Map<String, List<String>> = emptyMap(),
) {

    /**
     * 获取 AAR 趋势表格的 x 轴标签
     */
    fun getChartLabels(): Array<String> {
        return aarList.map {
            it.first
        }.toTypedArray().reversedArray()
    }

    /**
     * 获取 AAR 趋势表格的 y 轴数据
     */
    fun getChartSeries(owner: String, aarName: String): Pair<String, LongArray> {
        val series = LongArray(aarList.size)
        aarList.forEachIndexed { index, pair ->
            if (aarName == "All") {
                series[index] = pair.second.filter {
                    if (owner == "All") true else it.owner == owner
                }.totalSize()
            } else {
                series[index] = pair.second.find { it.name == aarName }?.size ?: 0
            }
        }
        return Pair("大小", series.reversedArray())
    }
}