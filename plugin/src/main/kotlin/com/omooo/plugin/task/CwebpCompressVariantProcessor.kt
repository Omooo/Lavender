package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.bean.CwebpCompressExtension
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2020/2/11
 * Version: v0.1.1
 * Desc: 注册 [CwebpCompressTask]
 */
@AutoService(VariantProcessor::class)
class CwebpCompressVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        project.tasks.register(
            "convert${variant.name.capitalize()}Webp",
            CwebpCompressTask::class.java
        ) {
            it.config = project.extensions.findByType(CwebpCompressExtension::class.java)
                ?: project.extensions.create(
                    "compressWebpConfig",
                    CwebpCompressExtension::class.java
                )
            it.variant = variant
            it.group = LAVENDER
            it.description = "Convert png image to webp"
            it.outputs.upToDateWhen { false }
        }.also {
            variant.mergeResourcesProvider.get().dependsOn(it)
        }
    }
}