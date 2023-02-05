package top.omooo.frontend.component

/**
 * Author: Omooo
 * Date: 2023/2/5
 * Desc:
 */
import csstype.TextAlign
import csstype.px
import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.useState

/**
 * Author: Omooo
 * Date: 2023/2/5
 * Desc: Owner 下拉选择器
 */

external interface OwnerSelectsProps : Props {
    var ownerList: List<String>
    var defaultSelect: String
    var onSelect: (String) -> Unit
}

val OwnerSelects = FC<OwnerSelectsProps> { props ->
    var owner by useState(props.defaultSelect)

    Box {
        sx {
            minWidth = 300.px
            marginRight = 18.px
            textAlign = TextAlign.center
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
                    props.onSelect(event.target.value)
                }
                props.ownerList.forEach {
                    MenuItem {
                        value = it
                        +it
                    }
                }
            }
        }
    }
}