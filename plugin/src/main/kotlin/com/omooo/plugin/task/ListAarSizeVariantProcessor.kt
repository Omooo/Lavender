package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2022/11/13
 * Version: v0.1.0
 * Desc: 注册 [ListAarSizeTask]
 */
@AutoService(VariantProcessor::class)
class ListAarSizeVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val project = variant.project
        if (project.tasks.findByName("listAarSize") != null) {
            return
        }
        project.tasks.register("listAarSize", ListAarSizeTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List the aar size in app project"
        }
    }

}