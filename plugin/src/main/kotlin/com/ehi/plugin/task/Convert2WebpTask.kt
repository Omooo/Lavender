package com.ehi.plugin.task

import com.ehi.plugin.ext.Convert2WebpExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

internal open class Convert2WebpTask : DefaultTask() {

    @TaskAction
    fun doAction() {
        val config = project.extensions.findByType(Convert2WebpExtension::class.java)
        println(config.toString())

        println("/**********  convert2WebpConfig  ***********/")
    }
}