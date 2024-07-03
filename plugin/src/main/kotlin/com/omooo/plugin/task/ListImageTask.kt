package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.HtmlReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.totalSize
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.getArtifactIdFromAarName
import com.omooo.plugin.util.isImageFile
import com.omooo.plugin.util.getAllChildren
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.project
import com.omooo.plugin.util.variantImpl
import com.omooo.plugin.util.versionName
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Author: Omooo
 * Date: 2023/05/25
 * Desc: 输出所有的图片资源
 * Use: ./gradlew listImage
 * Output: projectDir/imageList.json
 */
internal open class ListImageTask : DefaultTask() {
    @get:Internal
    lateinit var variant: Variant

    @TaskAction
    fun doAction() {
        println(
            """
                *********************************************
                ********** -- ListImageTask -- **************
                ****** -- projectDir/imageList.json -- ******
                *********************************************
            """.trimIndent()
        )
        AppReporter(
            desc = Insight.Title.LIST_IMAGE,
            documentLink = Insight.DocumentLink.LIST_IMAGE,
            versionName = variant.versionName,
            variantName = variant.name,
            aarList = getTotalImage(),
        ).apply {
            writeToJson("${project.parent?.projectDir}/imageList.json")
            HtmlReporter().generateReport(this, "${project.parent?.projectDir}/imageList.html")
        }
    }

    /**
     * 获取所有的图片
     */
    private fun getTotalImage(): List<AarFile> {
        val ownership = project.getOwnerShip()
        return variant.variantImpl.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.ANDROID_RES
        ).artifacts.associate { artifact ->
            // 处理 App 的所有依赖
            artifact.getArtifactName() to artifact.file.getAllChildren()
        }.plus(
            // 加上 App 工程的
            project.name to project.projectDir.resolve("src/main/res").getAllChildren()
        ).mapValues { entry ->
            entry.value.filter {
                it.isImageFile()
            }.sortedByDescending {
                it.length()
            }.map {
                AppFile(it.name, it.length())
            }
        }.filterValues {
            it.isNotEmpty()
        }.map {
            val owner = ownership.getOrDefault(it.key.getArtifactIdFromAarName(), "unknown")
            AarFile(it.key, it.value.totalSize(), owner, it.value.toMutableList())
        }.sortedByDescending { it.size }
    }

}