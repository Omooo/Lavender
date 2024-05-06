package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/5/24
 * Desc: 注册 [ListPackageNameTask]
 */
@Suppress("SwallowedException")
@AutoService(VariantProcessor::class)
class ListPackageNameVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val listPackageNameTask = try {
            variant.project.tasks.named("listPackageName")
        } catch (e: UnknownTaskException) {
            variant.project.tasks.register("listPackageName") {
                it.group = LAVENDER
                it.description = "List package name in app project."
            }
        }
        variant.project.tasks.register("listPackageNameFor${variant.nameCapitalize()}", ListPackageNameTask::class.java) {
            it.variant = variant
            it.apkFileCollection = variant.project.files(variant.artifacts.get(SingleArtifact.APK))
            it.group = LAVENDER
            it.description = "List package name for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }.also {
            listPackageNameTask.dependsOn(it)
        }
    }

}