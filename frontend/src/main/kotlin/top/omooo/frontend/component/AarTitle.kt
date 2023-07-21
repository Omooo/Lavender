package top.omooo.frontend.component

import csstype.AlignItems
import csstype.Border
import csstype.Color
import csstype.LineStyle
import csstype.px
import emotion.react.css
import mui.material.*
import react.FC
import react.Props
import top.omooo.frontend.bean.AarAnalyseReporter
import top.omooo.frontend.common.color
import top.omooo.frontend.common.xs
import top.omooo.frontend.util.formatSize

/**
 * Author: Omooo
 * Date: 2023/7/17
 * Desc: AAR 标题
 */

val AarTitle = FC<AarTitleProps> { props ->
    Box {
        css {
            border = Border(1.px, LineStyle.solid, Color("#C0C0C0"))
        }
        Grid {
            container = true
            css {
                alignItems = AlignItems.flexEnd
            }
            Grid {
                item = true
                xs = 8
                Box {
                    css {
                        padding = 16.px
                    }
                    Typography {
                        +props.data.packageName
                    }
                    Typography {
                        color = "text.secondary"
                        +"Version: v${props.data.versionName} (previous: v${
                            props.data.previousList.getOrNull(0)?.first
                        })"
                    }
                }
            }
            Grid {
                item = true
                xs = 2
                Box {
                    css {
                        padding = 16.px
                    }
                    Typography {
                        +"Total Count"
                    }
                    Typography {
                        color = "text.secondary"
                        +props.data.currentList.size.toString()
                    }
                }
            }
            Grid {
                item = true
                xs = 2
                Box {
                    css {
                        padding = 16.px
                    }
                    Typography {
                        +"Total Size"
                    }
                    Typography {
                        color = "text.secondary"
                        +props.data.currentList
                            .map { it.size }
                            .reduce { a, b -> a + b }
                            .formatSize()
                    }
                }
            }
        }
    }
}

external interface AarTitleProps : Props {
    var data: AarAnalyseReporter
}








