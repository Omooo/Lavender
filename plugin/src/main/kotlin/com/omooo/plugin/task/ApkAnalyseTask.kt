package com.omooo.plugin.task

import com.android.SdkConstants
import com.android.build.api.variant.Variant
import com.omooo.plugin.internal.apk.ApkIncrementAnalyse
import com.omooo.plugin.internal.apk.ApkParser
import com.omooo.plugin.internal.apk.clear
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.HtmlReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.FileType
import com.omooo.plugin.reporter.common.totalSize
import com.omooo.plugin.util.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: Apk 分析任务
 * Use: ./gradlew apkAnalyse
 * Output: projectDir/apkAnalyse.json
 */
internal abstract class ApkAnalyseTask : DefaultTask() {

    @get:Internal
    abstract var variant: Variant

    @get:InputDirectory
    abstract val apkFileDir: DirectoryProperty

    private val ownerShip: Map<String, String> by lazy {
        project.getOwnerShip()
    }

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                *********** -- ApkAnalyseTask -- ************
                ***** -- projectDir/apkAnalyse.json -- ******
                *********************************************
            """.trimIndent()
        )

        val startTime = System.currentTimeMillis()
        // 获取 APK 文件
        val apkFile = apkFileDir.get().asFileTree.first {
            it.extension == SdkConstants.EXT_ANDROID_PACKAGE
        }

        val appFileList = ApkParser().parse(apkFile).clear(variant)
        val currentAarList = getCurrentAarListFromApk(appFileList, getDependencies().filterNot {
            it.key == SdkConstants.ANDROID_MANIFEST_XML
        })
        val reporter = AppReporter(
            desc = Insight.Title.APK_ANALYSE,
            documentLink = Insight.DocumentLink.APK_ANALYSE,
            versionName = variant.versionName,
            variantName = variant.name,
            aarList = ApkIncrementAnalyse(project).analyse(currentAarList),
        )
        HtmlReporter().generateReport(reporter, "${project.parent?.projectDir}/apkAnalyse.html")
        println(green("Spend time: ${System.currentTimeMillis() - startTime}ms"))
    }

    /**
     * 产物分析
     *
     * @param appFileList 从 APK 里解析出的所有文件
     * @param dependencies 构建 APK 过程的所有依赖
     */
    private fun getCurrentAarListFromApk(
        appFileList: List<AppFile>,
        dependencies: Map<String, String>
    ): List<AarFile> {
        val aarFileList = appFileList.groupBy {
            val name = if (it.fileType == FileType.CLASS) {
                "${it.name.replace(".", "/")}.class"
            } else {
                it.name
            }
            // other 类型的文件，比如 AndroidManifest.xml、resources.arsc 都归属到壳工程
            dependencies.getOrDefault(name, "app-startup")
        }.map {
            AarFile(
                it.key,
                it.value.totalSize(),
                ownerShip.getOrDefault(it.key.getArtifactIdFromAarName(), "unknown"),
                it.value.sortedByDescending { it.size }.toMutableList()
            )
        }
        return aarFileList.sortedByDescending { it.size }
    }

    /**
     * 获取依赖的映射
     *
     * ag: "kotlin/io/path/LinkFollowing.class" to "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.21"
     * ag: "res/drawable/notification_tile_bg.xml" to "androidx.core:core:1.7.0"
     */
    private fun getDependencies(): Map<String, String> {
        variant.getDependencies().writeToJson("${project.parent?.projectDir}/getDependencies.json")
        return variant.getDependencies().flatMap { (key1, list) ->
            list.map { (key2, _) -> key2 to key1 }
        }.toMap()
    }
}