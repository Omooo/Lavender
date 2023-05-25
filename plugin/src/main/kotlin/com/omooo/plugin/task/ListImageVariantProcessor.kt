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
 * Date: 2023/5/25
 * Desc: 注册 [ListImageTask]
 */
@Suppress("SwallowedException", "DEPRECATION")
@AutoService(VariantProcessor::class)
class ListImageVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        val listImageTask = try {
            project.tasks.named("listImage")
        } catch (e: UnknownTaskException) {
            project.tasks.register("listImage") {
                it.group = LAVENDER
                it.description = "List image in app project."
            }
        }
        project.tasks.register("listImageFor${variant.nameCapitalize()}", ListImageTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List image for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }.also {
            it.dependsOn(variant.mergeResourcesProvider)
            listImageTask.dependsOn(it)
        }
    }
}