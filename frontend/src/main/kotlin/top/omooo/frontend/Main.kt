package top.omooo.frontend

import csstype.*
import emotion.react.css
import kotlinx.serialization.json.Json
import kotlinext.js.require
import mui.material.*
import mui.system.Box
import react.*
import react.dom.client.createRoot
import top.omooo.frontend.bean.AarAnalyseReporter
import top.omooo.frontend.bean.AarFile
import top.omooo.frontend.chart.ChartsComponent
import top.omooo.frontend.common.Header
import top.omooo.frontend.common.ThemeModule
import top.omooo.frontend.common.xs
import top.omooo.frontend.component.AarList
import top.omooo.frontend.component.AarTitle
import top.omooo.frontend.util.aarAnalyseMockData
import web.dom.document

fun main() {
    val text = require("./report.json").toString()
    val data = Json.decodeFromString(AarAnalyseReporter.serializer(), text)
//    val data = Json.decodeFromString(AarAnalyseReporter.serializer(), aarAnalyseMockData)
    createRoot(document.getElementById("root")!!).render(
        App.create {
            aarReporter = data
        }
    )
}

private external interface AppProps : Props {
    var aarReporter: AarAnalyseReporter
}

private val App = FC<AppProps> { props ->
    var owner by useState("All")
    var aarName by useState("")
    ThemeModule {
        Box {
            Header {
                title = props.aarReporter.desc
                documentLink = props.aarReporter.documentLink
            }

            AarTitle {
                data = props.aarReporter
            }

            TabComponent {
                aarReporter = props.aarReporter
                onSelect = { ownerName, aar ->
                    owner = ownerName
                    aarName = aar
                }
            }

        }
    }
}


private val TabComponent = FC<TabComponentProps> { props ->
    var activeTab by useState("one")
    var owner by useState("All")
    var aarName by useState("All")

    Box {
        Grid {
            container = true
            css {
                alignItems = AlignItems.flexEnd
            }
            Grid {
                item = true
                xs = 8
                Box {
                    Tabs {
                        value = activeTab
                        onChange = { _, newValue -> activeTab = newValue }

                        Tab {
                            value = "one"
                            label = ReactNode("统计")
                        }
                        Tab {
                            value = "two"
                            label = ReactNode("差异")
                        }
                        Tab {
                            value = "three"
                            label = ReactNode("趋势")
                        }
                    }
                }
            }

            Grid {
                item = true
                xs = 2
                Box {
                    css {
                        marginTop = 20.px
                        marginRight = 20.px
                    }
                    FormControl {
                        fullWidth = true
                        InputLabel {
                            id = "select-label"
                            +"Owner"
                        }
                        Select {
                            labelId = "select-label"
                            id = ""
                            value = owner
                            label = ReactNode("Owner")
                            onChange = { event, _ ->
                                owner = event.target.value
                                props.onSelect(event.target.value, "")
                            }
                            mutableListOf("All").plus(props.aarReporter.ownerMap.keys).forEach {
                                MenuItem {
                                    value = it
                                    +it
                                }
                            }
                        }
                    }
                }
            }

            Grid {
                item = true
                xs = 2
                Box {
                    css {
                        marginTop = 20.px
                        marginRight = 20.px
                    }
                    FormControl {
                        fullWidth = true
                        disabled = activeTab != "three"
                        InputLabel {
                            id = "select-label"
                            +"AAR"
                        }
                        Select {
                            labelId = "select-label"
                            id = ""
                            label = ReactNode("AAR")
                            onChange = { event, _ ->
                                aarName = event.target.value
                            }
                            val selectList = mutableListOf("All")
                            val aarList = props.aarReporter.aarList.getOrNull(0)?.second.orEmpty()
                            if (owner == "All") {
                                selectList.addAll(aarList.map { it.name })
                            } else {
                                selectList.addAll(aarList.filter { it.owner == owner }.map { it.name })
                            }
                            value = if (aarName in selectList) aarName else "All"
                            selectList.forEach {
                                MenuItem {
                                    value = it
                                    +it
                                }
                            }
                        }
                    }
                }
            }
        }

        when (activeTab) {
            "one" -> AarList {
                aarList = props.aarReporter.aarList.getOrNull(0)?.second.orEmpty().filter {
                    if (owner == "All") true else it.owner == owner
                }
            }
            "two" -> AarList {
                showDiff = true
                val ownerList =
                    if (owner == "All") props.aarReporter.ownerMap.keys else listOf(owner)
                val diffList =
                    props.aarReporter.aarList.getOrNull(1)?.second.orEmpty()
                        .filter { it.owner in ownerList }
                        .map { previousAar ->
                            val currentAar =
                                props.aarReporter.aarList.getOrNull(0)?.second.orEmpty().find {
                                    it.name == previousAar.name && it.owner in ownerList
                                }
                            AarFile(
                                previousAar.name,
                                (currentAar?.size ?: 0) - previousAar.size,
                                previousAar.owner
                            )
                        }.filter {
                            it.size != 0L
                        }
                aarList = diffList
            }
            "three" -> ChartsComponent {
                this.chartLabels = props.aarReporter.getChartLabels()
                this.chartSeries = props.aarReporter.getChartSeries(owner, aarName)
            }
        }
    }
}

private external interface TabComponentProps : Props {
    var aarReporter: AarAnalyseReporter
    var onSelect: (String, String) -> Unit
}