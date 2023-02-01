package top.omooo.frontend

import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML.h1
import web.dom.document

fun main() {
    createRoot(document.getElementById("root")!!).render(
        App.create()
    )
}

private val App = FC<Props> {
    h1 {
        +"Lavender"
    }
}