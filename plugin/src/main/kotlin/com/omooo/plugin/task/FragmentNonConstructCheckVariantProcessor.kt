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
 * Date: 2023/11/8
 * Desc: 注册 [FragmentNonConstructCheckTask]
 */
@AutoService(VariantProcessor::class)
class FragmentNonConstructCheckVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("checkFragmentNonConstruct") != null) {
            return
        }
        project.tasks.register("checkFragmentNonConstruct", FragmentNonConstructCheckTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Check Fragment non construct method in app project"
        }.also {
            it.dependsOn(project.getJarTaskProviders(variant))
        }

    }

}