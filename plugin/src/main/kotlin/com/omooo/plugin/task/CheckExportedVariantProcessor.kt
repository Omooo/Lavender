package com.omooo.plugin.task

import com.android.build.api.artifact.Artifact
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.getArtifactCollection
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.project
import com.omooo.plugin.util.variantImpl
import org.gradle.api.file.Directory

/**
 * Author: Omooo
 * Date: 2022/12/20
 * Desc: 注册 [CheckExportedTask]
 */
@AutoService(VariantProcessor::class)
class CheckExportedVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val project = variant.project
        project.tasks.register("checkExportedFor${variant.nameCapitalize()}", CheckExportedTask::class.java) {
            it.variant = variant
            it.manifests.set(variant.getArtifactCollection(AndroidArtifacts.ArtifactType.MANIFEST))
//            it.mergedManifest.set(variant.artifacts.get(SingleArtifact.MERGED_MANIFEST))
//            it.mainManifest.set(variant.variantImpl.sources.manifestFile)
            it.group = LAVENDER
            it.description = "Check exported attribute in Manifest."
            it.outputs.upToDateWhen { false }
        }
    }

}