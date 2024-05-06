package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2023/11/8
 * Desc: 注册 [FragmentNonConstructCheckTask]
 */
@AutoService(VariantProcessor::class)
class FragmentNonConstructCheckVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val project = variant.project
        if (project.tasks.findByName("checkFragmentNonConstruct") != null) {
            return
        }
        project.tasks.register("checkFragmentNonConstruct", FragmentNonConstructCheckTask::class.java) {
            it.variant = variant
            it.apkFileCollection = project.files(variant.artifacts.get(SingleArtifact.APK))
            it.group = LAVENDER
            it.description = "Check Fragment non construct method in app project"
        }

    }

}