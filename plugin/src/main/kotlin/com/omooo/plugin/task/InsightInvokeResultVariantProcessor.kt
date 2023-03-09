package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2023/2/23
 * Desc: 注册 [InsightInvokeResultTask]
 */
@AutoService(VariantProcessor::class)
class InsightInvokeResultVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("insightInvokeResult") != null) {
            return
        }
        project.tasks.register("insightInvokeResult", InsightInvokeResultTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Insight invoke check result in app project"
        }.also {
            it.dependsOn(project.tasks.named("assembleDebug"))
            it.get().mustRunAfter(project.tasks.named("assembleDebug").get())
        }

    }

}