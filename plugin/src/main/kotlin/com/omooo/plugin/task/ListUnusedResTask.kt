package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.util.getAarNameFromPath
import com.omooo.plugin.util.getAllChildren
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import kotlin.io.path.readText

/**
 * Author: Omooo
 * Date: 2022/12/14
 * Desc: 无用资源监测
 * Use: ./gradlew listUnusedRes
 * Output: projectDir/unusedRes.json
 */
internal open class ListUnusedResTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ********** -- ListUnusedResTask -- **********
                ****** -- projectDir/unusedRes.json -- ******
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        val resNameMap =
            (variant as ApplicationVariantImpl).variantData.allRawAndroidResources.files.flatMap {
                it.getAllChildren()
            }.associate {
                it.name to it.absolutePath.getAarNameFromPath(project.name)
            }
        getUnusedResName().takeIf {
            it.isNotEmpty()
        }?.groupBy {
            resNameMap.getOrDefault(it.substringAfterLast("/"), "unknown")
        }?.also {
            it.toSortedMap().writeToJson("${project.parent?.projectDir}/unusedRes.json")
        } ?: println("Unused resource is empty.")
    }

    /**
     * 获取未使用的资源名称
     * 从 resources.txt 文件中匹配出
     *
     * @return listOf("res/layout/xxx.xml", "res/drawable/xxx.webp", ...)
     */
    private fun getUnusedResName(): List<String> {
        return Files.walk(project.buildDir.resolve("outputs/mapping").toPath()).filter {
            it.fileName.toString() == "resources.txt"
        }.findFirst().takeIf {
            it.isPresent
        }?.get()?.let {
            "Skipped unused resource.+".toRegex().findAll(it.readText()).map { result ->
                result.groupValues
            }.flatten().map { line ->
                line.substring(line.indexOf("res/"), line.indexOf(":"))
            }.toList()
        } ?: emptyList()
    }

}