package com.ehi.plugin.task

import com.ehi.plugin.util.encode
import com.ehi.plugin.util.writeToJson
import org.gradle.api.DefaultTask
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
internal open class RepeatResDetectorTask : DefaultTask() {

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

        val map = HashMap<String, List<String>>()
        val prefix = if (project.properties["all"] != "true") "drawable-" else "drawable"
        project.projectDir.resolve("src/main/res").listFiles()?.filter {
            it.name.startsWith(prefix)
        }?.forEach { dir ->
            if (dir.isDirectory) {
                dir.listFiles()?.forEach { file ->
                    val key = file.readBytes().encode()
                    val value = arrayListOf<String>()
                    val list = map[key]
                    list?.let { it1 -> value.addAll(it1) }
                    value.add(file.absolutePath)
                    map[key] = value
                }
            }
        }
        var length: Long = 0
        map.filterValues { values ->
            values.size > 1
        }.apply {
            this.values.forEach {
                length += File(it[0]).length()
            }
            println("Repeat Res size: ${length / 1000}kb ")

            this.writeToJson("${project.parent?.projectDir}/repeatRes.json")
        }
    }
}