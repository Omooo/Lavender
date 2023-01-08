package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2022/12/20
 * Desc: 注册 [CheckExportedTask]
 */
@AutoService(VariantProcessor::class)
class CheckExportedVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        project.tasks.register("check${variant.name.capitalize()}Exported", CheckExportedTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Check exported attribute in Manifest."
            it.outputs.upToDateWhen { false }
        }.also {
            it.dependsOn(project.tasks.named("process${variant.name.capitalize()}Manifest"))
        }
    }

}