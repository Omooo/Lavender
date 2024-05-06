package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2023/3/8
 * Desc: 注册 [ListUnusedClassTask]
 */
@AutoService(VariantProcessor::class)
class ListUnusedClassVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        if (variant.name.contains("debug", true)){
            return
        }
        val project = variant.project
        project.tasks.register("listUnusedClassFor${variant.nameCapitalize()}", ListUnusedClassTask::class.java) {
            it.variant = variant
            it.apkFileCollection = variant.project.files(variant.artifacts.get(SingleArtifact.APK))
            it.group = LAVENDER
            it.description = "List unused class declared in application project for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }
    }

}