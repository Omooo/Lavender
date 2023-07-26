package top.omooo.frontend.chart


import org.w3c.dom.Element

typealias NumberFormatter = (Number) -> String
typealias TooltipAxisFormatter = (Number, TooltipAxisFormatterOptions) -> String

@JsModule("apexcharts")
@JsNonModule
@Suppress("UnusedPrivateMember")
external class ApexCharts(element: Element?, options: dynamic) {
    fun render()
    fun destroy()
}

external interface ApexChartOptions {
    var chart: ChartOptions
    var dataLabels: DataLabelOptions
    var fill: FillOptions
    var grid: GridOptions
    var legend: LegendOptions
    var plotOptions: PlotOptions
    var series: Array<Series>
    var stroke: StrokeOptions
    var tooltip: TooltipOptions
    var xaxis: AxisOptions
    var yaxis: AxisOptions
    var title: TitleOptions
//    var annotations: AnnotationsOptions
    var colors: Array<String>
}

external interface AnnotationsOptions {
    var yaxis: Array<AnnotationYaxisOptions>
}

external interface AnnotationYaxisOptions {
    var y: Long
    var y2: Long
    var borderColor: String
    var fillColor: String
    var label: AnnotationLabelOptions
    var strokeDashArray: Array<Float>
}

external interface AnnotationLabelOptions {
    var borderColor: String
    var text: String
    var style: AnnotationLabelStyleOptions
}

external interface AnnotationLabelStyleOptions {
    var color: String
    var background: String
}

external interface AxisLabelOptions {
    var style: AxisLabelStyleOptions
    var formatter: NumberFormatter
}

external interface AxisLabelStyleOptions {
    var fontSize: Int
}

external interface AxisOptions {
    var categories: Array<String>
    var labels: AxisLabelOptions
}

external interface BarPlotOptions {
    var horizontal: Boolean
}

external interface ChartOptions {
    var fontFamily: String
    var height: Int
    var toolbar: ToolbarOptions
    var type: String
    var zoom: ChartZoomOptions
}

external interface ChartZoomOptions {
    var enabled: Boolean
}

external interface DataLabelOptions {
    var enabled: Boolean
    var formatter: NumberFormatter
}

external interface FillOptions {
    var opacity: Double
}

external interface GridAxisLineOptions {
    var show: Boolean
}

external interface GridAxisOptions {
    var lines: GridAxisLineOptions
}

external interface GridRowOptions {
    var colors: Array<String>
    var opacity: Float
}

external interface GridOptions {
    var xaxis: GridAxisOptions
    var yaxis: GridAxisOptions
    var row: GridRowOptions
}

external interface LegendMarkerOptions {
    var width: Int
    var height: Int
}

external interface LegendOptions {
    var fontSize: Int
    var markers: LegendMarkerOptions
}

external interface PlotOptions {
    var bar: BarPlotOptions
}

external interface Series {
    var type: String
    var name: String
    var data: Array<Number>
}

external interface StrokeOptions {
    var show: Boolean
    var colors: Array<String>
    var width: Int
    var curve: String
}

external interface ToolbarOptions {
    var show: Boolean
}

external interface TooltipAxisFormatterOptions {
    var series: Array<Array<Number>>
    var seriesIndex: Int
}

external interface TooltipAxisOptions {
    var formatter: TooltipAxisFormatter
}

external interface TooltipOptions {
    var x: TooltipAxisOptions
    var y: TooltipAxisOptions
}

external interface TitleOptions {
    var text: String
    var align: String
}



