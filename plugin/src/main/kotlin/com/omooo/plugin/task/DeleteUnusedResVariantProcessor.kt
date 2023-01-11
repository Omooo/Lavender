package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2022/01/10
 * Desc: 注册 [DeleteUnusedResTask]
 */
@AutoService(VariantProcessor::class)
class DeleteUnusedResVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("deletedUnusedRes") != null) {
            return
        }
        project.tasks.register("deletedUnusedRes", DeleteUnusedResTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Delete unused res in app project"
        }

    }

}
