package top.omooo.frontend.component

import csstype.*
import mui.icons.material.ExpandMore
import mui.material.*
import mui.material.Size
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import top.omooo.frontend.bean.AarFile
import top.omooo.frontend.bean.AppFile
import top.omooo.frontend.util.formatSize

/**
 * Author: Omooo
 * Date: 2023/2/1
 * Desc: Aar 可展开列表
 */

external interface AarAccordionProps : Props {
    var aarList: List<AarFile>
}

val AarAccordion = FC<AarAccordionProps> { props ->
    props.aarList.forEach { aarData ->
        Accordion {
            sx {
                paddingLeft = 20.px
                flexGrow = number(1.0)
            }
            AccordionSummary {
                expandIcon = ExpandMore.create()
                Typography {
                    +aarData.name
                }
                Chip {
                    sx {
                        marginLeft = 18.px
                    }
                    size = Size.small
                    label = ReactNode(aarData.owner)
                    variant = ChipVariant.outlined
                }
                Typography {
                    sx {
                        flexGrow = number(1.0)
                        marginRight = 5.px
                        textAlign = TextAlign.right
                    }
                    +aarData.size.formatSize()
                }
            }
            AccordionDetails {
                AppFileList {
                    list = aarData.fileList.sortedByDescending {
                        it.size
                    }
                }
            }
        }
    }
}

external interface AppFileListProps : Props {
    var list: List<AppFile>
}

private val AppFileList = FC<AppFileListProps> { props ->
    List {
        sx {
            paddingRight = 10.px
        }
        props.list.forEach { appFile ->
            ListItem {
                ListItemText {
                    +appFile.name
                }
                ListItemText {
                    sx {
                        textAlign = TextAlign.right
                    }
                    +appFile.size.formatSize()
                }
            }
        }
    }
}