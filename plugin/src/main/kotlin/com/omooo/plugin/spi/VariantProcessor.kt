package com.omooo.plugin.spi

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: Task 注册接口
 */
interface VariantProcessor {

    fun process(project: Project, variant: BaseVariant)

}