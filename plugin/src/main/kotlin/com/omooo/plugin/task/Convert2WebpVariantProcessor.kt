package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.bean.Convert2WebpExtension
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2020/2/11
 * Version: v0.1.1
 * Desc: 注册 Convert2WebpTask
 * @see Convert2WebpTask
 */
@AutoService(VariantProcessor::class)
class Convert2WebpVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("convert2Webp") != null) {
            return
        }
        project.tasks.register("convert2Webp", Convert2WebpTask::class.java) {
            it.config = project.extensions.create("convert2WebpConfig", Convert2WebpExtension::class.java)
            it.variant = variant
            it.group = LAVENDER
            it.description = "Convert png image to webp"
        }.also {
            variant.mergeResourcesProvider.get().dependsOn(it)
        }

    }
}