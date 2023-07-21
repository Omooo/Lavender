package top.omooo.frontend.chart

import kotlinx.browser.document
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.useEffect

val ChartsComponent = FC<Props> {
    div {
        id = "id-charts"
        val s1 = LongArray(3)
        s1[0] = 6
        s1[1] = 6
        s1[2] = 6
        val s2 = LongArray(3)
        s2[0] = 4
        s2[1] = 1
        s2[2] = 10
        val config = LineChartConfig(
            chartLabels = arrayOf("v5.17.0", "v5.18.0", "v5.18.0"),
            chartSeries = arrayOf(
                seriesOf("name1", s2),
                seriesOf("基线", s1),
            ),
            chartHeight = 350,
            yAxisFormatter = ::formatSize,
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