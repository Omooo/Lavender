package top.omooo.frontend.chart

import js.core.jso

fun seriesOf(name: String, data: LongArray, type: String = ""): Series = jso {
    this.type = "line"
    this.name = name
    this.data = data.map(Long::toInt).toTypedArray()
}

fun annotationYaxisOptionsOf(y: Long, desc: String): AnnotationYaxisOptions = jso {
    this.y = y
    this.borderColor = "#00E396"
    this.fillColor = "#00E396"
    this.label = jso {
        this.text = desc
        this.borderColor = "#00E396"
        this.style = jso {
            color = "#FFFFFF"
            background = "#00E396"
        }
    }
    this.strokeDashArray = arrayOf(5f, 5f)
}
