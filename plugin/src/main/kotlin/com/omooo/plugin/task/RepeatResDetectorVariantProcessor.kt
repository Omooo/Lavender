package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: 注册 RepeatResDetectorTask
 * @see RepeatResDetectorTask
 */
@AutoService(VariantProcessor::class)
class RepeatResDetectorVariantProcessor : VariantProcessor {
    override fun process(variant: Variant) {
        val project = variant.project
        if (project.tasks.findByName("repeatRes") != null) {
            return
        }
        project.tasks.register("repeatRes", RepeatResDetectorTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Check repeat resources in app project"
        }
    }
}