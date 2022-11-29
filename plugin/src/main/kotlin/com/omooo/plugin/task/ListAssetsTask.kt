package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.util.getAllChildren
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Author: Omooo
 * Date: 2022/11/17
 * Desc: 输出所有的 Assets 资源
 * Use: ./gradlew listAssets
 * Output: projectDir/assets.json
 */
internal open class ListAssetsTask : DefaultTask() {
    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun doAction() {
        println(
            """
                *********************************************
                ********** -- ListAssetsTask -- *************
                ******* -- projectDir/assets.json -- ********
                *********************************************
            """.trimIndent()
        )
        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        getTotalAssets().writeToJson("${project.parent?.projectDir}/assets.json")
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
     * Asset 文件数据类
     */
    internal data class AssetFile(
        /** 文件名 */
        val fileName: String,
        /** 大小，单位 byte */
        val size: Long,
    )
}