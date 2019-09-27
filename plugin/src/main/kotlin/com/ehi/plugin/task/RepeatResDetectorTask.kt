package com.ehi.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

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
    }
}