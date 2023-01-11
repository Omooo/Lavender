package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import org.gradle.api.Project

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: 注册 ListPermissionTask
 * @see ListPermissionTask
 */
@AutoService(VariantProcessor::class)
class ListPermissionVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (project.tasks.findByName("listPermissions") != null) {
            return
        }
        project.tasks.register("listPermissions", ListPermissionTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List the permission declared in AndroidManifest.xml"
        }.also {
            it.dependsOn(project.tasks.named("processDebugManifest"))
            it.get().mustRunAfter(project.tasks.named("processDebugManifest").get())
        }
    }

}