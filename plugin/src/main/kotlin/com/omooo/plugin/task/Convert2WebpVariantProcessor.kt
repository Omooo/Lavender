package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.ext.Convert2WebpExtension
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
        if (project.extensions.findByType(Convert2WebpExtension::class.java) != null) {
            return
        }
        project.extensions.create("convert2WebpConfig", Convert2WebpExtension::class.java)
//        val variantData = (variant as ApplicationVariantImpl).variantData
//        val tasks = variantData.scope.globalScope.project.tasks
//        val convert2WebpTask = tasks.findByName("convert2Webp") ?: tasks.create(
//            "convert2Webp",
//            Convert2WebpTask::class.java
//        )
//        val mergeResourcesTask = variant.mergeResourcesProvider.get()
//        mergeResourcesTask.dependsOn(convert2WebpTask)
    }
}