package top.omooo.frontend.common

import mui.material.GridProps
import mui.material.TypographyProps

/**
 * Author: Omooo
 * Date: 2023/7/17
 * Desc: Remove when it will be implemented in MUI wrappers
 */

inline var GridProps.xs: Int
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().xs = value
    }

inline var TypographyProps.color: String
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().color = value
    }