package top.omooo.frontend.chart

/** Chart config for bar charts. */
@Suppress("LongParameterList")
class BarChartConfig(
    private val chartLabels: Array<String>,
    private val chartSeries: Array<Series>,
    private val chartHeight: Int,
    private val horizontal: Boolean = false,
    private val xAxisFormatter: NumberFormatter = Number::toString,
    private val yAxisFormatter: NumberFormatter = Number::toString,
    private val chartSeriesTotals: LongArray? = null,
) : ChartConfig() {

    override fun getOptions() = buildOptions {
        series = chartSeries
        xaxis.categories = chartLabels
        chart.type = "bar"
        chart.height = chartHeight

        grid.xaxis.lines.show = horizontal
        grid.yaxis.lines.show = !horizontal
        plotOptions.bar.horizontal = horizontal

        xaxis.labels.formatter = xAxisFormatter
        yaxis.labels.formatter = yAxisFormatter
        tooltip.y.formatter = ::formatTooltip
    }

    private fun formatTooltip(number: Number, options: TooltipAxisFormatterOptions): String {
        val axisFormatter = if (horizontal) xAxisFormatter else yAxisFormatter
        val total = if (chartSeriesTotals != null) {
            chartSeriesTotals[options.seriesIndex]
        } else {
            options.series[options.seriesIndex].sumOf(Number::toLong)
        }
        return "${axisFormatter.invoke(number)} (${formatPercentage(number, total)})"
    }
}
