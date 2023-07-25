package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2023/07/25
 * Desc: 注册 [AarAnalyseTask]
 */
@AutoService(VariantProcessor::class)
class AarAnalyseTaskProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("aarAnalyse") != null) {
            return
        }
        project.tasks.register("aarAnalyse", AarAnalyseTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Analyse the aar size in app project"
        }
    }

}