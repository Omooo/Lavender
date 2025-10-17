package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.getArtifactCollection
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.project
import com.omooo.plugin.util.variantImpl
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/08/25
 * Desc: 注册 [CheckServiceTypeTask]
 */
@AutoService(VariantProcessor::class)
class CheckServiceTypeVariantProcessor : VariantProcessor {

    @Suppress("SwallowedException")
    override fun process(variant: Variant) {
        val project = variant.project
        val checkServiceTypeTask = try {
            project.tasks.named("checkServiceType")
        } catch (e: UnknownTaskException) {
            project.tasks.register("checkServiceType") {
                it.group = LAVENDER
                it.description = "Check set foreground service type attribute in Manifest."
            }
        }
        project.tasks.register("checkServiceTypeFor${variant.nameCapitalize()}", CheckServiceTypeTask::class.java) {
            it.variant = variant
            it.manifests.set(variant.getArtifactCollection(AndroidArtifacts.ArtifactType.MANIFEST))
            it.mergedManifest.set(variant.artifacts.get(SingleArtifact.MERGED_MANIFEST))
//            it.mainManifest.set(variant.variantImpl.sources.manifestFile)
            it.group = LAVENDER
            it.description = "Check set foreground service type attribute in Manifest for ${variant.nameCapitalize()}"
            it.outputs.upToDateWhen { false }
        }.also {
            checkServiceTypeTask.dependsOn(it)
        }
    }

}