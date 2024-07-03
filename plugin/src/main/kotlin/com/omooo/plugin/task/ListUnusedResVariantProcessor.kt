package com.omooo.plugin.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.omooo.plugin.spi.VariantProcessor
import com.google.auto.service.AutoService
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.util.nameCapitalize
import com.omooo.plugin.util.project
import org.gradle.api.DefaultTask
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

    override fun process(variant: Variant) {
        if (variant.name.contains("debug", true)) {
            return
        }
        val project = variant.project
        val listUnusedResTask = try {
            project.tasks.named("listUnusedRes")
        } catch (e: UnknownTaskException) {
            project.tasks.register("listUnusedRes") {
                it.group = LAVENDER
                it.description = "List unused res in app project"
            }
        }
        project.tasks.register("listUnusedResFor${variant.nameCapitalize()}", ListUnusedResTask::class.java) {
            it.variant = variant
            it.apkFileCollection = project.files(variant.artifacts.get(SingleArtifact.APK))
            it.group = LAVENDER
            it.description = "List unused res for ${variant.name} in app project"
        }.also {
            listUnusedResTask.dependsOn(it)
        }

        if (project.properties.containsKey("strictMode")) {
            project.tasks.register("strictTaskInternalFor${variant.nameCapitalize()}", StrictTask::class.java) {
                it.variant = variant
            }.also {
//                project.tasks.named("shrink${variant.nameCapitalize()}Res").apply {
//                    this.dependsOn(it)
//                    this.get().mustRunAfter(it)
//                }
            }
        }

    }

}

/**
 * 设置 shrinkResources 严格模式 Task
 */
internal open class StrictTask : DefaultTask() {

    @get:Internal
    lateinit var variant: Variant

    @TaskAction
    fun run() {
        val resDir = File("${project.buildDir.absolutePath}/intermediates/merged-not-compiled-resources/${variant.flavorName}/${variant.buildType}")
        if (!resDir.exists() && !resDir.isDirectory) {
            println("merged-not-compiled-resources/${variant.flavorName}/${variant.buildType} dir is not exists.")
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