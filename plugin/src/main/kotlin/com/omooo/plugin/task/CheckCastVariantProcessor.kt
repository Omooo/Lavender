package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2025/10/19
 * Desc: 注册 [CheckCastTask]
 */
@AutoService(VariantProcessor::class)
class CheckCastVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val project = variant.project
        if (project.tasks.findByName("checkCast") != null) {
            return
        }
        project.tasks.register("checkCast", CheckCastTask::class.java) {
            it.variant = variant
            it.apkFileCollection = project.files(variant.artifacts.get(SingleArtifact.APK))
            it.group = LAVENDER
            it.description = "Check cast in app project"
        }

    }

}