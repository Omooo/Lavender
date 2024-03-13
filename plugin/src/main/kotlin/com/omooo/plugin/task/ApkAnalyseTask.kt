package com.omooo.plugin.task

import com.android.SdkConstants
import com.android.build.api.artifact.SingleArtifact
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
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
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import kotlin.io.path.extension

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: Apk 分析任务
 * Use: ./gradlew apkAnalyse
 * Output: projectDir/apkAnalyse.json
 */
internal open class ApkAnalyseTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

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

        if (variant !is ApplicationVariantImpl) {
            println(red("${variant.name} is not an application variant."))
            return
        }
        val startTime = System.currentTimeMillis()
        // 获取 APK 文件
        val apkFile =
            Files.walk(variant.component.artifacts.get(SingleArtifact.APK).get().asFile.toPath())
                .filter {
                    it.extension == SdkConstants.EXT_ANDROID_PACKAGE
                }.findFirst().get().toFile()

        val appFileList = ApkParser().parse(apkFile).clear(variant)
        val currentAarList = getCurrentAarListFromApk(appFileList, getDependencies().filterNot {
            it.key == SdkConstants.ANDROID_MANIFEST_XML
        })
        val reporter = AppReporter(
            desc = Insight.Title.APK_ANALYSE,
            documentLink = Insight.DocumentLink.APK_ANALYSE,
            versionName = (variant as ApplicationVariantImpl).versionName,
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
        return (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.AAR_OR_JAR
        ).artifacts.map { artifact ->
            artifact.getArtifactName() to artifact.file.parseAar().map { it.first }
        }.flatMap { pair ->
            pair.second.map { it to pair.first }
        }.associateBy({ it.first }, { it.second })
    }
}