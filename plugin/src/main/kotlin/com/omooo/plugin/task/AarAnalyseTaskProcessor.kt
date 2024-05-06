package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2023/07/25
 * Desc: 注册 [AarAnalyseTask]
 */
@AutoService(VariantProcessor::class)
class AarAnalyseTaskProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        if (variant.project.tasks.findByName("aarAnalyse") != null) {
            return
        }
        variant.project.tasks.register("aarAnalyse", AarAnalyseTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Analyse the aar size in app project"
        }
    }

}