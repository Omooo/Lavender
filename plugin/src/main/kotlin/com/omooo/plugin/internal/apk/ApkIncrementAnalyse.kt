package com.omooo.plugin.internal.apk

import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.totalSize
import com.omooo.plugin.util.writeToJson
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import java.io.File

/**
 * Author: Omooo
 * Date: 2023/4/3
 * Desc: APK 增量分析
 */
internal class ApkIncrementAnalyse(private val project: Project) {

    private val previousDataPath: String by lazy {
        "${project.parent?.projectDir}/lavender-plugin/apk/previous.json"
    }

    /**
     * 增量分析
     *
     * @return 返回差异列表
     */
    fun analyse(currentList: List<AarFile>): List<AarFile> {
        val previousList = getPreviousAarFileList()
        if (previousList.isEmpty() || project.hasProperty("forceRefresh")) {
            val f = File(previousDataPath)
            if (f.exists()) {
                f.delete()
            }
            f.mkdirs()
            return currentList.apply {
                writeToJson(previousDataPath)
            }
        }
        val map = previousList.associateBy { it.name.substringBeforeLast(":") }
        val result = mutableListOf<AarFile>()
        currentList.forEach { aarFile ->
            // 过滤掉版本号
            // com.xxx:xx:2.8.0 -> com.xxx:xx
            val aarId = aarFile.name.substringBeforeLast(":")
            if (map.containsKey(aarId)) {
                separateChange(aarFile.fileList, map[aarId]!!.fileList).takeIf {
                    it.isNotEmpty()
                }?.let {
                    result.add(
                        AarFile(aarFile.name, it.totalSize(), aarFile.owner, it.toMutableList())
                    )
                }
            } else {
                result.add(aarFile)
            }
        }
        return result.sortedByDescending { it.size }
    }

    /**
     * 分离变更
     */
    private fun separateChange(cList: List<AppFile>, pList: List<AppFile>): List<AppFile> {
        val map = pList.associateBy { it.name.substringBeforeLast(":") }
        return cList.filterNot {
            map.containsKey(it.name.substringBeforeLast(":"))
        }
    }

    private fun getPreviousAarFileList(): List<AarFile> {
        val jsonFile = project.parent?.projectDir?.resolve(previousDataPath)
        if (jsonFile?.exists() == true && jsonFile.readText().isNotEmpty()) {
            return Json.decodeFromString(ListSerializer(AarFile.serializer()), jsonFile.readText())
        }
        return emptyList()
    }
}