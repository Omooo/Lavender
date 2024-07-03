package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2022/11/17
 * Version: v0.0.1
 * Desc: 注册 [ListAssetsTask]
 */
@AutoService(VariantProcessor::class)
class ListAssetsVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val project = variant.project
        if (project.tasks.findByName("listAssets") != null) {
            return
        }
        project.tasks.register("listAssets", ListAssetsTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List all asset files in app project"
        }
    }

}