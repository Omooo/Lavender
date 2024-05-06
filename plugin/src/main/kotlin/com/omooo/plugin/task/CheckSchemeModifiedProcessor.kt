package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.CheckSchemeModifiedExtension
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.getArtifactCollection
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/8/21
 * Desc: 注册 [CheckSchemeModifiedTask]
 */
@AutoService(VariantProcessor::class)
class CheckSchemeModifiedProcessor : VariantProcessor {

    @Suppress("SwallowedException")
    override fun process(variant: Variant) {
        val project = variant.project
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
            it.manifests.set(variant.getArtifactCollection(AndroidArtifacts.ArtifactType.MANIFEST))
            it.mergedManifest.set(variant.artifacts.get(SingleArtifact.MERGED_MANIFEST))
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
            task.dependsOn(it)
        }
    }

}