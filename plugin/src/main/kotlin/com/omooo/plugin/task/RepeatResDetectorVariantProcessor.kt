package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: 注册 RepeatResDetectorTask
 * @see RepeatResDetectorTask
 */
@AutoService(VariantProcessor::class)
class RepeatResDetectorVariantProcessor : VariantProcessor {
    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("repeatRes") != null) {
            return
        }
        project.tasks.register("repeatRes", RepeatResDetectorTask::class.java) {
            it.group = LAVENDER
            it.description = "Check repeat resources in app project"
        }
    }
}