package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.processManifestTaskProvider
import com.omooo.plugin.util.project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: 注册 ListPermissionTask
 * @see ListPermissionTask
 */
@AutoService(VariantProcessor::class)
class ListPermissionVariantProcessor : VariantProcessor {

    override fun process(variant: Variant) {
        val project = variant.project
        val listPermissionsTask = try {
            project.tasks.named("listPermissions")
        } catch (e: UnknownTaskException) {
            project.tasks.register("listPermissions") {
                it.group = LAVENDER
                it.description = "List the permission declared in AndroidManifest.xml"
            }
        }
        project.tasks.register("listPermissionsFor${variant.nameCapitalize()}", ListPermissionTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List the permission declared in AndroidManifest.xml for ${variant.name}."
            it.outputs.upToDateWhen { false }
        }.also {
            it.dependsOn(variant.processManifestTaskProvider)
            listPermissionsTask.dependsOn(it)
        }
    }

}