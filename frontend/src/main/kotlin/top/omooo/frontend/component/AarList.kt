package top.omooo.frontend.component

import csstype.*
import mui.material.Divider
import mui.material.List
import mui.material.ListItem
import mui.material.ListItemText
import mui.system.sx
import react.FC
import react.Props
import top.omooo.frontend.bean.AarFile
import top.omooo.frontend.util.formatSize

/**
 * Author: Omooo
 * Date: 2023/7/17
 * Desc: Aar 列表
 */

val AarList = FC<AarListProps> { props ->
    List {
        sx {
            paddingRight = 10.px
        }
        props.aarList.forEach { appFile ->
            ListItem {
                ListItemText {
                    +appFile.name
                }
                ListItemText {
                    sx {
                        textAlign = TextAlign.right
                        if (props.showDiff) {
                            color = if (appFile.size < 0) Color("#4095E5") else Color("#BD3124")
                        }
                    }
                    +"${if (!props.showDiff || appFile.size < 0) "" else "+"}${appFile.size.formatSize()}"
                }
            }
            Divider()
        }
    }
}

external interface AarListProps : Props {
    var showDiff: Boolean
    var aarList: List<AarFile>
}
