package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.nameCapitalize
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/3/8
 * Desc: 注册 [InsightInvokeResultTask]
 */
@AutoService(VariantProcessor::class)
class ListUnusedClassVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (variant.name.contains("debug", true)){
            return
        }
        val listPermissionsTask = try {
            project.tasks.named("listUnusedClass")
        } catch (e: UnknownTaskException) {
            project.tasks.register("listUnusedClass") {
                it.group = LAVENDER
                it.description = "List unused class declared in application project"
            }
        }
        project.tasks.register("listUnusedClassFor${variant.nameCapitalize()}", ListUnusedClassTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List unused class declared in application project for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }.also {
            it.dependsOn(project.tasks.named("assemble${variant.nameCapitalize()}"))
            listPermissionsTask.dependsOn(it)
        }
    }

}