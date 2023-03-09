package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.*
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.writeToJson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

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
        val classMap = (variant as ApplicationVariantImpl).getClassMap().mapValues {
            it.value.first
        }

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