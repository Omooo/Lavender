package top.omooo.frontend.chart

import js.core.jso

/** Base config for displaying charts. Check https://apexcharts.com/docs/options/ for all chart types and options. */
abstract class ChartConfig {

    /** Returns the chart options for this config used by ApexCharts. */
    abstract fun getOptions(): ApexChartOptions

    /** Utility function which allows concrete configs to start with a common sets of defaults. */
    protected fun buildOptions(builder: ApexChartOptions.() -> Unit) = jso<ApexChartOptions> {
        chart = jso {
            fontFamily = FONT_FAMILY
            toolbar = jso {
                show = false
            }
            zoom = jso {
                enabled = false
            }
        }
        dataLabels = jso {
            enabled = false
            formatter = jso()
        }
        fill = jso {
            opacity = 1.0
        }
        grid = jso {
            xaxis = jso {
                lines = jso()
            }
            yaxis = jso {
                lines = jso()
            }
            row = jso {
                colors = jso()
                opacity = jso()
            }
        }
        legend = jso {
            fontSize = FONT_SIZE
            markers = jso {
                width = FONT_SIZE
                height = FONT_SIZE
            }
        }
        plotOptions = jso {
            bar = jso()
        }
        stroke = jso {
            show = true
            colors = arrayOf("transparent")
            width = STROKE_WIDTH
            curve = jso()
        }
        tooltip = jso {
            x = jso()
            y = jso()
        }
        xaxis = jso {
            labels = jso {
                style = jso {
                    fontSize = FONT_SIZE
                }
            }
        }
        yaxis = jso {
            labels = jso {
                style = jso {
                    fontSize = FONT_SIZE
                }
            }
        }
        title = jso {
            text = jso()
            align = jso()
        }
//        annotations = jso {
//            yaxis = arrayOf(
//                jso {
//                    y = jso()
//                    y2 = jso()
//                    borderColor = jso()
//                    fillColor = jso()
//                    label = jso {
//                        borderColor = jso()
//                        text = jso()
//                        style = jso {
//                            color = jso()
//                            background = jso()
//                        }
//                    }
//                    strokeDashArray = jso()
//                }
//            )
//        }
        colors = jso()
    }.apply(builder)

    private companion object {
        const val FONT_FAMILY = "var(--bs-body-font-family)"
        const val FONT_SIZE = 14
        const val STROKE_WIDTH = 3
    }
}
