package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Author: Omooo
 * Date: 2022/12/14
 * Desc: 注册 [ListUnusedResTask]
 */
@AutoService(VariantProcessor::class)
class ListUnusedResVariantProcessor : VariantProcessor {

    override fun process(project: Project, variant: BaseVariant) {
        if (variant.name.lowercase().contains("debug")) {
            return
        }
        if (project.tasks.findByName("listUnusedRes") != null) {
            return
        }
        project.tasks.register("listUnusedRes", ListUnusedResTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List unused res in app project"
        }.also {
            it.dependsOn(project.tasks.named("assembleRelease"))
            it.get().mustRunAfter(project.tasks.named("assembleRelease").get())
        }

        if (project.properties.containsKey("strictMode")) {
            project.tasks.register("strictTaskInternal", StrictTask::class.java) {
                it.variant = variant
            }.also {
                project.tasks.named("shrink${variant.name.capitalize()}Res").apply {
                    this.dependsOn(it)
                    this.get().mustRunAfter(it)
                }
            }
        }

    }

}

/**
 * 设置 shrinkResources 严格模式 Task
 */
internal open class StrictTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        val resDir = File("${project.buildDir.absolutePath}/intermediates/merged-not-compiled-resources/${variant.name}/")
        if (!resDir.exists() && !resDir.isDirectory) {
            project.logger.info("merged-not-compiled-resources/${variant.name} dir is not exists.")
        }
        File("$resDir/xml").apply {
            if (!exists()) {
                mkdir()
            }
            File(this, "lavender-keep-${System.currentTimeMillis()}.xml")
                .writeText(KEEP_STRICT_RES_CONTENT)
        }
    }

}

private const val KEEP_STRICT_RES_CONTENT = """<?xml version="1.0" encoding="utf-8"?>
<resources
    xmlns:tools="http://schemas.android.com/tools"
    tools:shrinkMode="strict" />
"""