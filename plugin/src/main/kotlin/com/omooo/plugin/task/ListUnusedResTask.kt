package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.getAllChildren
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.writeToJson
import kotlinx.serialization.json.Json
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

        val ownerMap = project.getOwnerShip()
        val resNameMap = getResMap()
        getUnusedResName().takeIf {
            it.isNotEmpty()
        }?.groupBy {
            resNameMap[it]?.first ?: "unknown"
        }?.mapValues {
            it.value.map { resName ->
                AppFile(resName, resNameMap[resName]?.second ?: 0)
            }
        }?.filterValues {
            it.isNotEmpty()
        }?.map { entry ->
            val size = entry.value.map { it.size }.reduce { acc, l -> acc + l }
            AarFile(entry.key, size, ownerMap.getOrDefault(entry.key, "unknown"), entry.value.toMutableList())
        }?.also { aarFileList ->
            val appReporter = AppReporter(
                desc = "${LAVENDER.capitalize()} - List Unused Res",
                documentLink = "",
                versionName = (variant as ApplicationVariantImpl).versionName,
                variantName = variant.name,
                aarList = aarFileList.sortedByDescending { it.size }
            )
            val reduceSize = appReporter.aarList.map { it.size }.reduce { acc, l -> acc + l }
            println("Total reduce size: $reduceSize bytes.")
            Json.encodeToString(AppReporter.serializer(), appReporter).writeToJson(
                "${project.parent?.projectDir}/unusedRes.json"
            )
        } ?: println(
            """
            Unused resource is empty.
            May be resource shrinking did not work.
            Please try use './gradlew listUnusedRes -PstrictMode' instead.
        """.trimIndent()
        )
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

    /**
     * 资源名映射
     *
     * @return Map<资源名称, Pair<AAR 名, 资源大小>>
     */
    private fun getResMap(): Map<String, Pair<String, Long>> {
        val resMap =
            (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.ANDROID_RES
            ).artifacts.associate { artifact ->
                artifact.getArtifactName() to artifact.file.getAllChildren()
                    .sortedByDescending { file ->
                        file.length()
                    }.map {
                        AppFile("res/${it.absolutePath.substringAfterLast("res/")}", it.length())
                    }
            }.toMutableMap().also { map ->
                project.projectDir.resolve("src/main/res").takeIf {
                    it.isDirectory
                }?.getAllChildren()?.sortedByDescending { file ->
                    file.length()
                }?.also {
                    map[project.name] = it.map { file ->
                        AppFile(
                            "res/${file.absolutePath.substringAfterLast("res/")}",
                            file.length()
                        )
                    }
                }
            }
        // 处理成更好的调用方式
        return resMap.filterValues {
            it.isNotEmpty()
        }.flatMap { entry ->
            entry.value.map {
                it.name to Pair(entry.key, it.size)
            }
        }.toMap()
    }
}