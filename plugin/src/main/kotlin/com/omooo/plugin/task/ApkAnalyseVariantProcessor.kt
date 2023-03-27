package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.nameCapitalize
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: 注册 [ApkAnalyseTask]
 */
@AutoService(VariantProcessor::class)
class ApkAnalyseVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (variant.name.contains("debug", true)) {
            return
        }
        val analyseTask = try {
            project.tasks.named("apkAnalyse")
        } catch (e: UnknownTaskException) {
            project.tasks.register("apkAnalyse") {
                it.group = LAVENDER
                it.description = "Analyse the apk output from app project"
            }
        }
        project.tasks.register(
            "apkAnalyseFor${variant.nameCapitalize()}",
            ApkAnalyseTask::class.java
        ) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Analyse the apk output from app project for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }.also {
            it.dependsOn(project.tasks.named("assemble${variant.nameCapitalize()}"))
            analyseTask.dependsOn(it)
        }
    }

}