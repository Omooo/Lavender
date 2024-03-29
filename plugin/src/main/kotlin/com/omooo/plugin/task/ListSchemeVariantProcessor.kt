package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.getJarTaskProviders
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.processManifestTaskProvider
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/3/16
 * Desc: 注册 [ListSchemeTask]
 */
@AutoService(VariantProcessor::class)
class ListSchemeVariantProcessor : VariantProcessor {

    @Suppress("SwallowedException")
    override fun process(project: Project, variant: BaseVariant) {
        val listPermissionsTask = try {
            project.tasks.named("listSchemes")
        } catch (e: UnknownTaskException) {
            project.tasks.register("listSchemes") {
                it.group = LAVENDER
                it.description = "List the schemes declared in AndroidManifest.xml"
            }
        }
        project.tasks.register("listSchemesFor${variant.nameCapitalize()}", ListSchemeTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List the schemes declared in AndroidManifest.xml for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }.also {
            // 因为要归属是负责该 scheme，所以需要依赖 JarTask
            it.dependsOn(project.getJarTaskProviders(variant).toMutableList().apply {
                variant.processManifestTaskProvider?.let { it1 -> add(it1) }
            })
            listPermissionsTask.dependsOn(it)
        }

    }

}