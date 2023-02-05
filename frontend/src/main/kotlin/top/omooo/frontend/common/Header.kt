package top.omooo.frontend.common

import csstype.integer
import csstype.number
import kotlinx.browser.window
import mui.icons.material.Brightness4
import mui.icons.material.Brightness7
import mui.icons.material.MenuBook
import mui.material.*
import mui.material.styles.TypographyVariant.h6
import mui.system.sx
import react.*
import react.dom.html.ReactHTML.div

/**
 * Author: Omooo
 * Date: 2023/2/1
 * Desc: 通用顶部 Header
 */

external interface HeaderProps : Props {
    /** 标题 */
    var title: String

    /** 文档链接 */
    var documentLink: String
}

val Header = FC<HeaderProps> { props ->
    var theme by useContext(ThemeContext)

    AppBar {
        position = AppBarPosition.sticky
        sx {
            zIndex = integer(1_500)
        }

        Toolbar {
            Typography {
                sx { flexGrow = number(1.0) }
                variant = h6
                noWrap = true
                component = div

                +props.title
            }

            Tooltip {
                title = ReactNode("Theme")

                Switch {
                    icon = Brightness7.create()
                    checkedIcon = Brightness4.create()
                    checked = theme == Themes.Dark

                    onChange = { _, checked ->
                        theme = if (checked) Themes.Dark else Themes.Light
                    }
                }
            }

            Tooltip {
                title = ReactNode("Read Documentation")

                IconButton {
                    size = Size.large
                    color = IconButtonColor.inherit
                    onClick = {
                        window.open(props.documentLink)
                    }

                    MenuBook()
                }
            }
        }
    }
}