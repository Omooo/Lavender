package top.omooo.frontend.component

import csstype.Position
import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import react.create

/**
 * Author: Omooo
 * Date: 2023/2/3
 * Desc:
 */

external interface SummaryProps : Props {
    var title: String
    var subtitle: String
    var ownerList: List<String>
    var defaultSelect: String
    var onSelect: (String) -> Unit
}

val Summary = FC<SummaryProps> { props ->
    Alert {
        sx {
            position = Position.sticky
        }
        severity = AlertColor.info
        color = AlertColor.info

        AlertTitle {
            +props.title
        }
        +props.subtitle

        if (props.ownerList.isNotEmpty()) {
            action = OwnerSelects.create {
                ownerList = props.ownerList
                defaultSelect = props.defaultSelect
                onSelect = props.onSelect
            }
        }
    }
}