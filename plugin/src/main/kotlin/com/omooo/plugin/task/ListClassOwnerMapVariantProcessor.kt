package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.getJarTaskProviders
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2023/3/15
 * Desc: 注册 [ListClassOwnerMapTask]
 */
@AutoService(VariantProcessor::class)
class ListClassOwnerMapVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("listClassOwnerMap") != null) {
            return
        }
        project.tasks.register("listClassOwnerMap", ListClassOwnerMapTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List classes owner map in app project"
        }.also {
            it.dependsOn(project.getJarTaskProviders(variant))
        }

    }

}