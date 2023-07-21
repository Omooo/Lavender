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

        yaxis.labels.formatter = yAxisFormatter

        stroke.apply {
            show = true
            curve = "straight"
            colors = arrayOf("#4095E5")
        }
        title.apply {
            text = ""
            align = "left"
        }
        annotations.yaxis = arrayOf(annotationYaxisOptionsOf(5, "基线"))
    }

}
