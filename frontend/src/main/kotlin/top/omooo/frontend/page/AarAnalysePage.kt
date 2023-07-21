package top.omooo.frontend.page

import kotlinext.js.require
import kotlinx.serialization.json.Json
import mui.system.Box
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.useState
import top.omooo.frontend.bean.AarAnalyseReporter
import top.omooo.frontend.common.Header
import top.omooo.frontend.common.ThemeModule
import top.omooo.frontend.component.AarAccordion
import top.omooo.frontend.component.Summary
import top.omooo.frontend.util.formatSize
import web.dom.document

/**
 * Author: Omooo
 * Date: 2023/7/14
 * Desc: AAR 分析页面
 */

fun main() {
    val text = require("./aarAnalyse.json").toString()
    val data = Json.decodeFromString(AarAnalyseReporter.serializer(), text)
    createRoot(document.getElementById("root")!!).render(
        App.create {
            aarReporter = data
        }
    )
}

external interface AppProps : Props {
    var aarReporter: AarAnalyseReporter
}

private val App = FC<AppProps> { props ->
    var owner by useState("none")
    ThemeModule {
        Box {
            Header {
                title = props.aarReporter.desc
                documentLink = props.aarReporter.documentLink
            }

            Summary {
                title = props.aarReporter.currentList.filter {
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
                subtitle = props.aarReporter.let {
                    "Version: ${it.versionName} (previous: ${it.versionName})"
                }
                ownerList = mutableListOf("none").apply {
                    addAll(props.aarReporter.currentList.map { it.owner }.toSet())
                }
                defaultSelect = "none"
                onSelect = {
                    owner = it
                }
            }

            AarAccordion {
                aarList = props.aarReporter.currentList.filter {
                    if (owner == "none") true else it.owner == owner
                }
            }
        }
    }
}