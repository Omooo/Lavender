package top.omooo.frontend.chart

import kotlinx.browser.document
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffect
import top.omooo.frontend.util.formatSize
import kotlin.math.roundToLong

val ChartsComponent = FC<ChartsComponentProps> { props->
    div {
        id = "id-charts"
        val thresholdSize = props.chartSeries.second.find {
            it != 0L
        }?.times(1.2f)?.roundToLong() ?: 0L
        val thresholdSeries = props.chartSeries.second.map {
            if (it != 0L) thresholdSize else it
        }.toLongArray()
        val config = LineChartConfig(
            chartLabels = props.chartLabels,
            chartSeries = arrayOf(
                seriesOf(props.chartSeries.first, props.chartSeries.second),
                seriesOf("阈值", thresholdSeries),
            ),
            chartHeight = 350,
            yAxisFormatter = Number::formatSize,
        )
        useEffect {
            val chart = ApexCharts(document.getElementById("id-charts"), config.getOptions())
            chart.render()
            cleanup {
                chart.destroy()
            }
        }

    }
}

external interface ChartsComponentProps : Props {
    var chartSeries: Pair<String, LongArray>
    var chartLabels: Array<String>
}