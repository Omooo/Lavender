package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2022/01/11
 * Desc: 注册 [ListUnusedAssetsTask]
 */
@AutoService(VariantProcessor::class)
class ListUnusedAssetsVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val project = variant.project
        if (variant.name.lowercase().contains("debug")) {
            return
        }
        if (project.tasks.findByName("listUnusedAssets") != null) {
            return
        }
        project.tasks.register("listUnusedAssets", ListUnusedAssetsTask::class.java) {
            it.variant = variant
            it.apkFileCollection = project.files(variant.artifacts.get(SingleArtifact.APK))
            it.group = LAVENDER
            it.description = "List unused assets in app project"
        }.also {
//            it.dependsOn(project.tasks.named("assembleRelease"))
//            it.get().mustRunAfter(project.tasks.named("assembleRelease").get())
        }

    }

}