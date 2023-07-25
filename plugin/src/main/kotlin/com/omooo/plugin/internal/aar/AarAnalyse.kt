package com.omooo.plugin.internal.aar

import com.omooo.plugin.reporter.AarAnalyseReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.util.getArtifactIdFromAarName
import com.omooo.plugin.util.getOwnerMap
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.writeToJson
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import java.io.File

/**
 * Author: Omooo
 * Date: 2023/07/25
 * Desc: AAR 配额分析
 */
internal class AarAnalyse(private val project: Project) {

    private val previousDataPath: String by lazy {
        "${project.parent?.projectDir}/lavender-plugin/aar/previous.json"
    }

    /**
     * 增量、趋势分析
     *
     * @param pkgName 包名
     * @param currentAarPair 当前 AAR 数据
     *
     * @return 返回分析报告
     */
    fun analyse(pkgName: String, currentAarPair: Pair<String, List<AarFile>>): AarAnalyseReporter {
        val ownerMap = project.getOwnerMap()
        val reporter = getPreviousReporter().apply {
            this.packageName = pkgName
            if (this.aarList.getOrNull(0)?.first == currentAarPair.first) {
                this.aarList.removeAt(0)
            }
            this.aarList.add(0, currentAarPair)
            // 说明 owner 配置文件发生变更，则需要重新给 AAR 打 owner 标签
            if (ownerMap != this.ownerMap) {
                this.ownerMap = ownerMap
                val ownership = project.getOwnerShip()
                aarList.flatMap { it.second }.forEach {
                    it.owner = ownership.getOrDefault(it.name.getArtifactIdFromAarName(), "unknown")
                }
            }
        }
        if (project.hasProperty("forceRefresh")) {
            val f = File(previousDataPath)
            if (f.exists()) {
                f.delete()
            }
            f.mkdirs()
            reporter.writeToJson(previousDataPath)
        }
        return reporter
    }

    /**
     * 获取上一个版本的报告
     */
    private fun getPreviousReporter(): AarAnalyseReporter {
        val jsonFile = project.parent?.projectDir?.resolve(previousDataPath)
        if (jsonFile?.exists() == true && jsonFile.readText().isNotEmpty()) {
            return Json.decodeFromString(AarAnalyseReporter.serializer(), jsonFile.readText())
        }
        return AarAnalyseReporter(
            desc = Insight.Title.AAR_ANALYSE,
            documentLink = Insight.DocumentLink.AAR_ANALYSE,
            packageName = "",
            aarList = ArrayList(),
            ownerMap = project.getOwnerMap(),
        )
    }
}