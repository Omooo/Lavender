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
 * Date: 2023/3/17
 * Desc: 注册 [ApkAnalyseTask]
 */
@AutoService(VariantProcessor::class)
class ApkAnalyseVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        if (variant.name.contains("debug", true)) {
            return
        }
        variant.project.tasks.register(
            "apkAnalyseFor${variant.nameCapitalize()}",
            ApkAnalyseTask::class.java
        ) {
            it.variant = variant
            it.apkFileDir.set(variant.artifacts.get(SingleArtifact.APK))
            it.group = LAVENDER
            it.description = "Analyse the apk output from app project for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }
    }

}