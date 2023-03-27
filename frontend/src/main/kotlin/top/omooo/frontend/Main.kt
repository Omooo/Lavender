package top.omooo.frontend

import kotlinext.js.require
import kotlinx.serialization.json.Json
import mui.system.Box
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.useState
import top.omooo.frontend.bean.AppReporter
import top.omooo.frontend.common.Header
import top.omooo.frontend.common.ThemeModule
import top.omooo.frontend.component.AarAccordion
import top.omooo.frontend.component.Summary
import top.omooo.frontend.util.*
import web.dom.document

fun main() {
    val text = require("./report.json").toString()
    val data = Json.decodeFromString(AppReporter.serializer(), text)
    createRoot(document.getElementById("root")!!).render(
        App.create {
            appReporter = data
        }
    )
}

external interface AppProps : Props {
    var appReporter: AppReporter
}

private val App = FC<AppProps> { props ->
    var owner by useState("none")
    ThemeModule {
        Box {
            Header {
                title = props.appReporter.desc
                documentLink = props.appReporter.documentLink
            }

            Summary {
                title = props.appReporter.aarList.filter {
                    if (owner == "none") true else it.owner == owner
                }.let { list ->
                    if ((list.firstOrNull()?.size ?: 0) > 0) {
                        "A total of ${list.size} components belong to $owner, including ${
                            list.map { it.size }.reduce { acc, l -> acc + l }.formatSize()
                        } of unused resources."
                    } else {
                        "A total of ${list.size} components belong to $owner, including ${
                            list.map { it.fileList.size }.reduce { acc, l -> acc + l }
                        } classes need to check."
                    }
                }
                subtitle = props.appReporter.let {
                    "Version: ${it.versionName} (${it.variantName})"
                }
                ownerList = mutableListOf("none").apply {
                    addAll(props.appReporter.aarList.map { it.owner }.toSet())
                }
                defaultSelect = "none"
                onSelect = {
                    owner = it
                }
            }

            AarAccordion {
                aarList = props.appReporter.aarList.filter {
                    if (owner == "none") true else it.owner == owner
                }
            }
        }
    }
}