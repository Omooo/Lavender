package com.omooo.plugin.util

import com.android.build.gradle.api.BaseVariant
import java.util.*

/**
 * Author: Omooo
 * Date: 2023/3/8
 * Desc: [Variant] 相关扩张函数
 */

internal fun BaseVariant.nameCapitalize(): String {
    return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}