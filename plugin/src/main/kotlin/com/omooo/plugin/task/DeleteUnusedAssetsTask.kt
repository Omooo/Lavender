package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.bean.KEY_ARTIFACT_ID
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.util.getArtifactIdFromAarName
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.File

/**
 * Author: Omooo
 * Date: 2022/02/06
 * Desc: 无用 Assets 删除
 * Use: ./gradlew deleteUnusedAssets
 */
internal open class DeleteUnusedAssetsTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant
    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ******** -- DeleteUnusedAssetsTask -- *******
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }

        if (getUnusedResJson().isNullOrEmpty()) {
            println("File: jar/reporter/unusedAssets.json not found.")
            return
        }
        val json = getUnusedResJson()
        val unusedResMap: Map<String, List<String>> =
            Json.decodeFromString(AppReporter.serializer(), json!!).aarList.associate { aarFile ->
                aarFile.name.getArtifactIdFromAarName() to aarFile.fileList.map { it.name }
            }
        project.rootProject.subprojects.associateWith {
            // 如果没有定义 POM_ARTIFACT_ID，则使用当前工程的名字作为 artifact id
            if (it.properties.containsKey(KEY_ARTIFACT_ID)) {
                it.properties[KEY_ARTIFACT_ID].toString()
            } else {
                project.name
            }
        }.mapValues {
            val key = if (unusedResMap.containsKey(it.value)) it.value else "jetified-${it.value}"
            unusedResMap.getOrDefault(key, emptyList()).map { resName ->
                "${it.key.projectDir.absolutePath}/src/main/assets/$resName"
            }
        }.filterValues {
            it.isNotEmpty()
        }.forEach { (project, unusedResList) ->
            println("Project: ${project.name}")
            println("   contains ${unusedResList.size} unused resources.")
            var reduceSize = 0L
            unusedResList.forEach { path ->
                File(path).takeIf { it.exists() }?.apply {
                    reduceSize += this.length()
                    this.delete()
                }?: println("   file does not exist: $path.")
            }
            if (unusedResList.isNotEmpty()) {
                println("   total reduce size: $reduceSize bytes.")
            }
        }
    }

    /**
     * 从 unusedRes.json 文件中读取无用资源列表
     */
    private fun getUnusedResJson(): String? {
        return javaClass.classLoader.getResourceAsStream("reporter/unusedAssets.json")
            ?.bufferedReader()
            ?.use(BufferedReader::readText)
    }
}