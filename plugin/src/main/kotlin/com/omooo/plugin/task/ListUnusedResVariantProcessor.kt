package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
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
        if (variant.name.toLowerCase().contains("debug")) {
            return
        }
        val listUnusedResTask = try {
            project.tasks.named("listUnusedRes")
        } catch (e: UnknownTaskException) {
            project.tasks.register("listUnusedRes") {
                it.group = LAVENDER
                it.description = "List unused res in app project"
            }
        }
        project.tasks.register("listUnusedResFor${variant.name.capitalize()}", ListUnusedResTask::class.java) {
            it.variant = variant
            it.group = LAVENDER
            it.description = "List unused res for ${variant.name} in app project"
        }.also {
            listUnusedResTask.dependsOn(it)
            it.dependsOn(project.tasks.named("assembleRelease"))
            it.get().mustRunAfter(project.tasks.named("assembleRelease").get())
        }

        if (project.properties.containsKey("strictMode")) {
            project.tasks.register("strictTaskInternalFor${variant.name.capitalize()}", StrictTask::class.java) {
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
        val resDir = File("${project.buildDir.absolutePath}/intermediates/merged-not-compiled-resources/${variant.flavorName}/${variant.buildType.name}")
        if (!resDir.exists() && !resDir.isDirectory) {
            println("merged-not-compiled-resources/${variant.flavorName}/${variant.buildType.name} dir is not exists.")
            return
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