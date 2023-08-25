package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.CheckSchemeModifiedExtension
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.getJarTaskProviders
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.processManifestTaskProvider
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/8/21
 * Desc: 注册 [CheckSchemeModifiedTask]
 */
@AutoService(VariantProcessor::class)
class CheckSchemeModifiedProcessor : VariantProcessor {

    @Suppress("SwallowedException")
    override fun process(project: Project, variant: BaseVariant) {
        val task = try {
            project.tasks.named("checkSchemeModified")
        } catch (e: UnknownTaskException) {
            project.tasks.register("checkSchemeModified") {
                it.group = LAVENDER
                it.description = "Check the schemes modified might trigger compile failure"
            }
        }
        project.tasks.register(
            "checkSchemeModifiedFor${variant.nameCapitalize()}",
            CheckSchemeModifiedTask::class.java
        ) {
            it.variant = variant
            it.config = project.extensions.findByType(CheckSchemeModifiedExtension::class.java)
                ?: project.extensions.create(
                    "checkSchemeModifiedConfig",
                    CheckSchemeModifiedExtension::class.java
                )
            it.group = LAVENDER
            it.description =
                "Check the schemes modified might trigger compile failure for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }.also {
            // 因为要归属是谁修改了 scheme，所以需要依赖 JarTask
            it.dependsOn(project.getJarTaskProviders(variant).toMutableList().apply {
                variant.processManifestTaskProvider?.let { it1 -> add(it1) }
            })
            task.dependsOn(it)
        }
    }

}