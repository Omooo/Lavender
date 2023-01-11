package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.task.ListAssetsTask.AssetFile
import com.omooo.plugin.util.getAllChildren
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.writeToJson
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
        referencedStrings.forEach {
            println("referencing: $it")
        }
        if (referencedStrings.isEmpty()) {
            println("Not support shrinkResources shrinkMode is strict.")
            return
        }
        val result = mutableMapOf<String, List<String>>()
        var reduceSize = 0L
        getTotalAssets().forEach { entry ->
            entry.value.filterNot { assetFile ->
                referencedStrings.contains(assetFile.fileName)
            }.takeIf { list ->
                list.isNotEmpty()
            }?.run {
                result[entry.key] = this.map { it.fileName }
                reduceSize += this.map { it.size }.reduce { acc, l -> acc + l }
            }
        }
        // 写入结果
        result.takeIf {
            it.isNotEmpty()
        }?.apply {
            println("Total reduce size: $reduceSize")
            result.writeToJson("${project.parent?.projectDir}/unusedAssets.json")
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
                    AssetFile(it.absolutePath.substringAfterLast("out/"), it.length())
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