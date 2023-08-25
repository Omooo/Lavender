package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.nameCapitalize
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException

/**
 * Author: Omooo
 * Date: 2023/08/25
 * Desc: 注册 [CheckServiceTypeTask]
 */
@AutoService(VariantProcessor::class)
class CheckServiceTypeVariantProcessor : VariantProcessor {

    @Suppress("SwallowedException")
    override fun process(project: Project, variant: BaseVariant) {
        val checkServiceTypeTask = try {
            project.tasks.named("checkServiceType")
        } catch (e: UnknownTaskException) {
            project.tasks.register("checkServiceType") {
                it.group = LAVENDER
                it.description = "Check set foreground service type attribute in Manifest."
            }
        }
        project.tasks.register("checkServiceTypeFor${variant.nameCapitalize()}", CheckServiceTypeTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "Check set foreground service type attribute in Manifest for ${variant.nameCapitalize()}"
            it.outputs.upToDateWhen { false }
        }.also {
            it.dependsOn(project.tasks.named("process${variant.nameCapitalize()}Manifest"))
            checkServiceTypeTask.dependsOn(it)
        }
    }

}