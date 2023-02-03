package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.bean.LAVENDER
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.Ownership
import com.omooo.plugin.task.ListAssetsTask.AssetFile
import com.omooo.plugin.util.getAllChildren
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.writeToJson
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

/**
 * Author: Omooo
 * Date: 2022/01/11
 * Desc: 无用 Assets 监测
 * Use: ./gradlew listUnusedAssets
 * Output: projectDir/unusedAssets.json
 */
internal open class ListUnusedAssetsTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ********* -- ListUnusedAssetsTask -- ********
                ***** -- projectDir/unusedAssets.json -- ****
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        val referencedStrings = getReferencedStrings()
        if (referencedStrings.isEmpty()) {
            println("Not support shrinkResources shrinkMode is strict.")
            return
        }
        val aarFileList = mutableListOf<AarFile>()
        val ownerMap = Ownership().getOwnerMap()
        var reduceSize = 0L
        getTotalAssets().forEach { entry ->
            entry.value.filterNot { assetFile ->
                // 当引用字符串长度超过 40 时会被截取
                // from: AGP 7.0 ResourceShrinkerModel#158
                val name = assetFile.fileName.let {
                    if (it.length > 40) "${it.substring(0, 37)}..." else it
                }
                referencedStrings.contains(name)
            }.takeIf { list ->
                list.isNotEmpty()
            }?.run {
                val s = this.map { it.size }.reduce { acc, l -> acc + l }
                reduceSize += s
                val artifactId = entry.key.substringBeforeLast(":").substringAfter(":")
                aarFileList += AarFile(entry.key, s, ownerMap.getOrDefault(artifactId, "unknown"), this.map {
                    AppFile(it.fileName, it.size)
                })
            }
        }
        // 写入结果
        aarFileList.takeIf {
            it.isNotEmpty()
        }?.apply {
            println("Total reduce size: $reduceSize bytes.")
            val appReporter = AppReporter(
                desc = "${LAVENDER.capitalize()} - List Unused Assets",
                documentLink = "",
                versionName = (variant as ApplicationVariantImpl).versionName,
                variantName = variant.name,
                aarList = aarFileList,
            )
            Json.encodeToString(AppReporter.serializer(), appReporter).writeToJson(
                "${project.parent?.projectDir}/unusedAssets.json"
            )
        } ?: println("Unused assets is empty.")
    }

    /**
     * 获取所有的 Assets 文件列表
     *
     * @return Map<Artifact 名称, [AssetFile]>
     */
    private fun getTotalAssets(): Map<String, List<AssetFile>> {
        return (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.ASSETS
        ).artifacts.associate { artifact ->
            artifact.getArtifactName() to artifact.file.getAllChildren()
                .sortedByDescending { file ->
                    file.length()
                }.map {
                    AssetFile(
                        it.absolutePath.substringAfterLast(
                            "out/",
                            it.absolutePath.substringAfterLast("assets/")
                        ),
                        it.length()
                    )
                }
        }.toMutableMap().also { map ->
            project.projectDir.resolve("src/main/assets").takeIf {
                it.isDirectory
            }?.getAllChildren()?.sortedByDescending { file ->
                file.length()
            }?.also {
                map[project.name] = it.map { file ->
                    AssetFile(file.absolutePath.substringAfterLast("assets/"), file.length())
                }
            }
        }
    }

    /**
     * 获取所有的引用字符串（从 resources.txt 文件中匹配出）
     */
    private fun getReferencedStrings(): List<String> {
        return Files.walk(project.buildDir.resolve("outputs/mapping").toPath()).filter {
            it.fileName.toString() == "resources.txt"
        }.findFirst().takeIf {
            it.isPresent
        }?.get()?.let {
            val result = mutableListOf<String>()
            Files.readAllLines(it).apply {
                val startLine = indexOf("Referenced Strings:") + 1
                for (i in startLine until size) {
                    if (!this[i].startsWith("Marking ")) {
                        result.add(this[i].trimStart())
                    } else {
                        break
                    }
                }
            }
            result
        } ?: emptyList()
    }

}