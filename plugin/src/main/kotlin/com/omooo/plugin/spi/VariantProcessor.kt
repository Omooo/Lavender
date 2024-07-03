package com.omooo.plugin.spi

import com.android.build.api.variant.Variant
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: Task 注册接口
 */
interface VariantProcessor {

    @Deprecated(
        message = "BaseVariant is deprecated,  please use process(variant: Variant) method instead",
        replaceWith = ReplaceWith(
            expression = "process(variant: Variant)"
        )
    )
    fun process(project: Project, variant: BaseVariant) = Unit

    fun process(variant: Variant) = Unit
}