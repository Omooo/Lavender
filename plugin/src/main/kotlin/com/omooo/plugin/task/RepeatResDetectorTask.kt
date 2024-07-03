package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType.ANDROID_RES
import com.omooo.plugin.util.encode
import com.omooo.plugin.util.getArtifactFiles
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: 重复资源监测
 * Use: ./gradlew detectRepeatRes
 * Output: projectDir/repeatRes.json
 */
internal abstract class RepeatResDetectorTask : DefaultTask() {

    @get:Internal
    lateinit var variant: Variant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ******** -- RepeatResDetectorTask -- ********
                ****** -- projectDir/repeatRes.json -- ******
                *********************************************
            """.trimIndent()
        )

        val resultMap = HashMap<String, ArrayList<String>>()
        val prefix = if (project.properties["all"] != "true") "drawable-" else "drawable"

        variant.getArtifactFiles(ANDROID_RES).plus(project.projectDir.resolve("src/main/res")).forEach { resDir ->
            resDir.listFiles()?.filter {
                it.isDirectory && it.name.startsWith(prefix)
            }?.forEach { drawableDir ->
                drawableDir.listFiles()?.filter {
                    !it.isDirectory
                }?.forEach { file ->
                    resultMap.getOrDefault(file.readBytes().encode(), arrayListOf()).apply {
                        add(file.absolutePath)
                    }.also {
                        resultMap[file.readBytes().encode()] = it
                    }
                }
            }
        }

        var totalSize: Long = 0
        resultMap.filterValues { values ->
            values.size > 1
        }.apply {
            this.values.forEach {
                totalSize += File(it[0]).length()
            }

            println("Repeat Res count: ${keys.size}, total size: ${totalSize / 1000}kb")
            this.writeToJson("${project.parent?.projectDir}/repeatRes.json")
        }
    }

}