package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.getArtifactCollection
import com.omooo.plugin.util.project
import com.omooo.plugin.util.variantImpl
import org.gradle.api.model.ObjectFactory

/**
 * Author: Omooo
 * Date: 2023/3/22
 * Desc: 注册 [DetectTranslucentActivityTask]
 */
@AutoService(VariantProcessor::class)
class DetectTranslucentActivityVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val project = variant.project
        if (project.tasks.findByName("detectTranslucentActivity") != null) {
            return
        }
        project.tasks.register(
            "detectTranslucentActivity",
            DetectTranslucentActivityTask::class.java
        ) {
            it.variant = variant
            it.manifests.set(variant.getArtifactCollection(AndroidArtifacts.ArtifactType.MANIFEST))
            it.apkFileCollection = project.files(variant.artifacts.get(SingleArtifact.APK))
            it.group = LAVENDER
            it.description = "Detect the translucent activity from app project"
            it.outputs.upToDateWhen { false }
        }
    }

}