package com.omooo.plugin.task

import com.android.SdkConstants
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
import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.streams.toList

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

//        val repackTask = project.tasks.register("repack", RepackTask::class.java) {
//            it.variant = variant
//        }
//        project.tasks.named("process${variant.name.capitalize()}Resources").apply {
//            repackTask.get().mustRunAfter(this.get())
//            this.get().finalizedBy(repackTask)
//        }
//        project.tasks.named("shrink${variant.name.capitalize()}Res").apply {
//            this.get().dependsOn(repackTask)
//            this.get().mustRunAfter(repackTask)
//        }

    }

}

internal open class RepackTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        Files.walk(File("${project.buildDir.absolutePath}/intermediates/processed_res/${variant.name}/out/").toPath())
            .toList().find {
                it.extension == SdkConstants.EXT_RES
            }?.toFile()?.repack()
    }

    private fun File.repack() {
        val tempDir = "${parent}/temp"
        ZipUtils.unzip(this, tempDir)
        File("$tempDir/res/raw").let {
            if (!it.exists()) {
                it.mkdir()
            }
            File(it, "lavender-keep1-${System.currentTimeMillis()}.xml")
                .writeText(KEEP_STRICT_RES_CONTENT)
        }

        ZipUtils.zipAll(tempDir, this.absolutePath)
    }
}

private const val KEEP_STRICT_RES_CONTENT = """<?xml version="1.0" encoding="utf-8"?>
<resources
    xmlns:tools="http://schemas.android.com/tools"
    tools:shrinkMode="strict" />
"""