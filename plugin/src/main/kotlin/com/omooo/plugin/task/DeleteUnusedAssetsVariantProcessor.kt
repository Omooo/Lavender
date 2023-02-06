package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2023/2/6
 * Desc: 注册 [DeleteUnusedAssetsTask]
 */
@AutoService(VariantProcessor::class)
class DeleteUnusedAssetsVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("deletedUnusedAssets") != null) {
            return
        }
        project.tasks.register("deletedUnusedAssets", DeleteUnusedAssetsTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Delete unused assets in app project"
        }

    }

}