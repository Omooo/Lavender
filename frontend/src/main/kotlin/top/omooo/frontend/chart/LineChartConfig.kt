package top.omooo.frontend.chart

/**
 * Chart config for line charts.
 * 文档：https://apexcharts.com/javascript-chart-demos/line-charts/basic/
 */
@Suppress("LongParameterList")
class LineChartConfig(
    private val chartLabels: Array<String>,
    private val chartSeries: Array<Series>,
    private val chartHeight: Int,
    private val xAxisFormatter: NumberFormatter = Number::toString,
    private val yAxisFormatter: NumberFormatter = Number::toString,
) : ChartConfig() {

    override fun getOptions() = buildOptions {
        series = chartSeries
        xaxis.categories = chartLabels
        chart.type = "line"
        chart.height = chartHeight
        chart.zoom.enabled = false

        grid.xaxis.lines.show = true

        grid.row.apply {
            colors = arrayOf("#f3f3f3", "transparent")
            opacity = 0.5f
        }

        dataLabels.enabled = true
        dataLabels.formatter = yAxisFormatter

        yaxis.labels.formatter = yAxisFormatter

        stroke.apply {
            show = true
            curve = "straight"
            colors = arrayOf("#4095E5", "#BD3124")
        }
        title.apply {
            text = "阈值为最初版本的 120% 大小"
            align = "middle"
        }
        colors = arrayOf("#4095E5", "#BD3124")
//        annotations.yaxis = arrayOf(annotationYaxisOptionsOf(5, "阈值"))
    }

}
