package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.getArtifactIdFromAarName
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.writeToJson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Author: Omooo
 * Date: 2023/2/23
 * Desc: 可视化调用检查任务的结果
 * Use: ./gradlew insightInvokeResult
 * Output: projectDir/invokeResultInsight.json
 */
internal open class InsightInvokeResultTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ******* -- InsightInvokeResultTask -- *******
                * -- projectDir/invokeResultInsight.json -- *
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }

        val json = project.getCheckReportJson()
        if (json.isNullOrEmpty()) {
            println("File: {projectDir}/lavender-plugin/reporter/invokeCheckReport.json not found.")
            return
        }

        val invokeResultMap = Json.decodeFromString<Map<String, List<String>>>(json)
        val ownerShip = project.getOwnerShip()
        val classMap = getClassMap()

        val aarMap = mutableMapOf<String, AarFile>()
        invokeResultMap.forEach { entry ->
            entry.value.forEach { invokePoint ->
                val aarName = classMap.getOrDefault(invokePoint.substringBefore("#"), "unknown")
                val owner = ownerShip.getOrDefault(aarName.getArtifactIdFromAarName(), "unknown")
                if (!aarMap.containsKey(aarName)) {
                    aarMap[aarName] = AarFile(aarName, 0, owner, mutableListOf())
                }
                val className = invokePoint.substringBefore("(").substringAfterLast(".")
                aarMap[aarName]?.fileList?.add(AppFile(name = className.replace("$", "/"), desc = entry.key))
            }
        }

        AppReporter(
            desc = Insight.Title.INVOKE_CHECK,
            documentLink = Insight.DocumentLink.INVOKE_CHECK,
            versionName = (variant as ApplicationVariantImpl).versionName,
            variantName = variant.name,
            aarList = aarMap.values.toList(),
        ).writeToJson("${project.parent?.projectDir}/invokeResultInsight.json")
    }

    /**
     * 类全限定名到 AAR 名的映射
     * 例如: androidx.core.graphics.PaintKt to androidx.core:core-ktx:1.7.0
     */
    private fun getClassMap(): Map<String, String> {
        return (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.CLASSES
        ).artifacts.associate { artifact ->
            artifact.getArtifactName() to artifact.file.parseJar()
        }.entries.flatMap { entry ->
            entry.value.map {
                it.substringBeforeLast(".").replace("/", ".") to entry.key
            }
        }.toMap()
    }


    /**
     * 解析 Jar 生成文件列表
     */
    private fun File.parseJar(): List<String> {
        return JarFile(this).entries().toList().filterNot(JarEntry::isDirectory).map {
            it.name
        }
    }

    /**
     * 从 invokeCheckReport.json 文件中读取调用点
     */
    private fun Project.getCheckReportJson(): String? {
        val file = parent?.projectDir?.resolve("lavender-plugin/reporter/invokeCheckReport.json")
        if (file != null && file.exists()) {
            return file.readText()
        }
        return null
    }
}