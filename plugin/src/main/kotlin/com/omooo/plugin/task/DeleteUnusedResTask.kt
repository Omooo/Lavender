package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File

/**
 * Author: Omooo
 * Date: 2022/01/10
 * Desc: 无用资源删除
 * Use: ./gradlew deleteUnusedRes
 * Output: projectDir/unusedResDeleted.json
 */
internal open class DeleteUnusedResTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ********* -- DeleteUnusedResTask -- *********
                *** -- projectDir/unusedResDeleted.json -- **
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }

        if (getUnusedResJson().isNullOrEmpty()) {
            println("File: jar/file/unusedRes.json not found.")
            return
        }
        val unusedResObj = JSONObject(getUnusedResJson())
        project.rootProject.subprojects.filter {
            it.properties.containsKey(KEY_ARTIFACT_ID)
        }.associateWith {
            it.properties[KEY_ARTIFACT_ID].toString()
        }.mapValues {
            unusedResObj.findUnusedResAbsolutePathList(
                it.key.projectDir.absolutePath,
                it.value
            )
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
     * 找出该子工程下的无用资源的绝对路径列表
     *
     * @param subProjectPath 子工程路径
     * @param artifactId 子工程 artifact id
     */
    private fun JSONObject.findUnusedResAbsolutePathList(
        subProjectPath: String,
        artifactId: String,
    ): List<String> {
        return keySet().find {
            it.startsWith(artifactId) || it.startsWith("jetified-$artifactId")
        }?.let {
            (getJSONArray(it).toList() as? List<String>)?.map { resName ->
                "$subProjectPath/src/main/$resName"
            } ?: emptyList()
        } ?: emptyList()
    }

    /**
     * 从 unusedRes.json 文件中读取无用资源列表
     */
    private fun getUnusedResJson(): String? {
        return javaClass.classLoader.getResourceAsStream("file/unusedRes.json")
            ?.bufferedReader()
            ?.use(BufferedReader::readText)
    }
}

/** gradle properties key artifact id */
private const val KEY_ARTIFACT_ID = "POM_ARTIFACT_ID"