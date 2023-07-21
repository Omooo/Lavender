package top.omooo.frontend

import csstype.*
import emotion.react.css
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
import web.dom.document

fun main() {
//    val text = require("./aarAnalyse.json").toString()
//    val data = Json.decodeFromString(AarAnalyseReporter.serializer(), text)

    val data = AarAnalyseReporter(
        "Lavender",
        "",
        "com.android",
        "5.18.0",
        listOf(
            AarFile("name-1", 100, "Owner - 1", emptyList()),
            AarFile("name-2", 200, "Owner - 1", emptyList()),
            AarFile("name-3", 300, "Owner - 2", emptyList()),
            AarFile("name-4", 400, "Owner - 4", emptyList()),
            AarFile("name-5", 500, "unknown", emptyList()),
            AarFile("name-6", 600, "Owner - 2", emptyList()),
            AarFile("name-7", 700, "Owner - 3", emptyList()),
            AarFile("name-8", 800, "Owner - 4", emptyList()),
        ),
        listOf(
            Pair(
                "5.17.5", listOf(
                    AarFile("name-1", 100, "Owner - 1", emptyList()),
                    AarFile("name-2", 500, "Owner - 1", emptyList()),
                    AarFile("name-3", 300, "Owner - 2", emptyList()),
                    AarFile("name-4", 100, "Owner - 4", emptyList()),
                    AarFile("name-5", 500, "unknown", emptyList()),
                    AarFile("name-6", 600, "Owner - 2", emptyList()),
                    AarFile("name-7", 300, "Owner - 3", emptyList()),
                    AarFile("name-8", 200, "Owner - 4", emptyList()),
                )
            ),
            Pair("5.17.0", listOf(AarFile("name-1", 100, "", emptyList()))),
        ),
        ownerMap = mapOf(
            Pair("Owner - 1", listOf("name-1", "name-2")),
            Pair("Owner - 2", listOf("name-3", "name-6")),
            Pair("Owner - 3", listOf("name-7")),
            Pair("Owner - 4", listOf("name-4", "name-8")),
        )
    )
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
                onSelect = { ownerName, aarName ->
                    owner = ownerName
                }
            }


        }
    }
}


private val TabComponent = FC<TabComponentProps> { props ->
    var activeTab by useState("one")
    var owner by useState("All")

    Box {
        Grid {
            container = true
            css {
                alignItems = AlignItems.flexEnd
            }
            Grid {
                item = true
                xs = 9
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
                xs = 3
                Box {
                    css {
                        marginTop = 20.px
                        marginRight = 20.px
                    }
                    FormControl {
                        fullWidth = true
                        InputLabel {
                            id = "select-label"
                            +"Owner & AAR"
                        }
                        Select {
                            labelId = "select-label"
                            id = ""
                            value = owner
                            label = ReactNode("Owner & AAR")
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
        }

        when(activeTab) {
            "one" -> AarList {
                aarList = props.aarReporter.currentList.filter {
                    if (owner == "All") true else it.owner == owner
                }
            }
            "two" -> AarList {
                showDiff = true
                val ownerList = if (owner == "All") props.aarReporter.ownerMap.keys else listOf(owner)
                val diffList =
                    props.aarReporter.previousList[0].second.filter { it.owner in ownerList }
                        .map { previousAar ->
                            val currentAar =
                                props.aarReporter.currentList.find {
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
            "three" -> ChartsComponent()
        }
    }
}

private external interface TabComponentProps : Props {
    var aarReporter: AarAnalyseReporter
    var onSelect: (String, String) -> Unit
}