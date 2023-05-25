package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.getJarTaskProviders
import com.omooo.plugin.util.nameCapitalize
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/5/24
 * Desc: 注册 [ListPackageNameTask]
 */
@Suppress("SwallowedException")
@AutoService(VariantProcessor::class)
class ListPackageNameVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        val listPackageNameTask = try {
            project.tasks.named("listPackageName")
        } catch (e: UnknownTaskException) {
            project.tasks.register("listPackageName") {
                it.group = LAVENDER
                it.description = "List package name in app project."
            }
        }
        project.tasks.register("listPackageNameFor${variant.nameCapitalize()}", ListPackageNameTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List package name for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }.also {
            it.dependsOn(project.getJarTaskProviders(variant))
            listPackageNameTask.dependsOn(it)
        }
    }

}