package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2023/3/22
 * Desc: 注册 [DetectTranslucentActivityTask]
 */
@AutoService(VariantProcessor::class)
class DetectTranslucentActivityVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("detectTranslucentActivity") != null) {
            return
        }
        project.tasks.register(
            "detectTranslucentActivity",
            DetectTranslucentActivityTask::class.java
        ) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Detect the translucent activity from app project"
            it.outputs.upToDateWhen { false }
        }.also {
            it.dependsOn(project.tasks.named("assembleDebug"))
        }
    }

}