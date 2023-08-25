package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.bean.CheckSchemeModifiedExtension
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.util.getOwner
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.getArtifactFiles
import com.omooo.plugin.util.getArtifactClassMap
import com.omooo.plugin.util.green
import com.omooo.plugin.util.parseSchemesFromManifest
import com.omooo.plugin.util.red
import com.omooo.plugin.util.writeToJson
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

/**
 * Author: Omooo
 * Date: 2023/8/21
 * Desc: 检查 Manifest 里定义的 scheme 集合，如果发生变更（修改和删除）则会触发编译失败
 * Use: ./gradlew checkSchemesModified
 */
internal open class CheckSchemeModifiedTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @get:Internal
    lateinit var config: CheckSchemeModifiedExtension

    @Suppress("ReturnCount")
    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ****** -- CheckSchemeModifiedTask -- ********
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println(red("${variant.name} is not an application variant."))
            return
        }

        if (!config.enable || config.baselineSchemeFile == null || !config.baselineSchemeFile!!.exists()) {
            println(red("Skip execute CheckSchemeModifiedTask, config: $config"))
            return
        }

        // 方便本地编译跳过检查，省着修改 build.gradle 重新 sync
        if (project.hasProperty("skipCheck")) {
            println(green("Skip execute CheckSchemeModifiedTask."))
            return
        }

        // 基线 scheme 列表
        val baselineSchemeMap: Map<String, Pair<String, List<String>>> = Json.decodeFromString(
            MapSerializer(String.serializer(), PairSerializer(String.serializer(), String.serializer())),
            config.baselineSchemeFile!!.readText()
        ).mapValues {
            Pair(it.value.first, it.value.second.split(", "))
        }

        // 当前 scheme 列表
        val currentSchemeMap: Map<String, List<String>> =
            (variant as ApplicationVariantImpl).getArtifactFiles(AndroidArtifacts.ArtifactType.MANIFEST)
                .map {
                    it.parseSchemesFromManifest()
                }.filter {
                    it.isNotEmpty()
                }.flatMap {
                    it.entries
                }.associate {
                    it.toPair()
                }.mapValues {
                    it.value.split(", ")
                }

        val diffMap = baselineSchemeMap.separateDiff(currentSchemeMap)
        if (diffMap.isEmpty()) {
            println(green("CheckSchemeModifiedTask execute success."))
            return
        }
        // 发现异常，输出日志，触发编译失败
        val ownerShip = project.getOwnerShip()
        val classOwnerMap = (variant as ApplicationVariantImpl).getArtifactClassMap().mapValues {
            ownerShip.getOwner(it.value.first)
        }
        currentSchemeMap.mapValues {
            Pair(classOwnerMap.getOrDefault(it.key, "unknown"), it.value.joinToString())
        }.writeToJson("${project.parent?.projectDir}/schemes.json")

        throw TaskExecutionException(this, IllegalStateException(green(diffMap.formatOutput())))
    }

    /**
     * 格式化输出
     */
    @Suppress("MaxLineLength")
    private fun Map<String, Pair<String, List<String>>>.formatOutput(): String {
        return buildString {
            appendLine("---------------------------------- ${Insight.Title.CHECK_SCHEME_MODIFIED} ----------------------------------")
            appendLine("发现下列 scheme 定义存在变更: ")

            this@formatOutput.forEach { (className, diffPair) ->
                appendLine(" ---")
                appendLine(" \tClass: $className")
                appendLine(" \tOwner: ${diffPair.first}")
                appendLine(" \tSchemeList: ${diffPair.second}")
            }

            appendLine(" ---")
            appendLine("如何解决，请参考文档: ${Insight.DocumentLink.CHECK_SCHEME_MODIFIED}")
            appendLine("本地编译跳过该任务检查: 增加 \"skipCheck\" 参数即可，例如: ./gradlew clean assembleDebug -PskipCheck")
            appendLine("------------------------------------------------------------------------------------------------------")
        }
    }

    /**
     * 分离出变更
     *
     * @param current 当前列表
     */
    private fun Map<String, Pair<String, List<String>>>.separateDiff(
        current: Map<String, List<String>>,
    ): Map<String, Pair<String, List<String>>> {
        val map = mutableMapOf<String, Pair<String, List<String>>>()
        val currentSchemeList = current.flatMap { it.value }
        this.entries.forEach { entry ->
            val diffSchemeList =
                (entry.value.second - current.getOrDefault(entry.key, emptyList()).toSet()).filter {
                    // 可能只是类更改了全限定名，但是 scheme 还是存在的，这种情况下过滤掉
                    !currentSchemeList.contains(it)
                }
            map[entry.key] = Pair(entry.value.first, diffSchemeList)
        }
        return map.filterValues { it.second.isNotEmpty() }
    }
}