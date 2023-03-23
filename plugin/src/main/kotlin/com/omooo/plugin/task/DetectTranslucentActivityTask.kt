package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: 检测半透明 Activity 任务 👇
 *       半透明 Activity 设置了 screenOrientation 的非 unspecified 属性时会 Crash
 * Use: ./gradlew detectTranslucentActivity
 * Output: projectDir/translucentActivity.json
 */
internal open class DetectTranslucentActivityTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                **** -- DetectTranslucentActivityTask -- ****
                * -- projectDir/translucentActivity.json -- *
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println(red("${variant.name} is not an application variant."))
            return
        }
        val v = variant as ApplicationVariantImpl
        val translucentStyleList = v.getTranslucentStyleList()
        val ownerShip = project.getOwnerShip()
        val classMap = v.getArtifactClassMap()
        v.getArtifactFiles(AndroidArtifacts.ArtifactType.MANIFEST).map {
            it.parseManifest()
        }.filter {
            it.isNotEmpty()
        }.reduce { acc, map ->
            acc.toMutableMap().apply { putAll(map) }
        }.toMap().filterValues {
            translucentStyleList.contains(it.substringAfter("/"))
        }.keys.groupBy {
            classMap.getOrDefault(it, null)?.first?: "unknown-aar-name"
        }.map {
            AarFile(
                it.key,
                0,
                ownerShip.getOrDefault(it.key.getArtifactIdFromAarName(), "unknown"),
                it.value.map { className -> AppFile(className) }.toMutableList()
            )
        }.run {
            AppReporter(
                desc = Insight.Title.DETECT_TRANSLUCENT_ACTIVITY,
                documentLink = Insight.DocumentLink.DETECT_TRANSLUCENT_ACTIVITY,
                versionName = (variant as ApplicationVariantImpl).versionName,
                variantName = variant.name,
                aarList = this,
            ).writeToJson("${project.parent?.projectDir}/translucentActivity.json")
        }
    }

    /**
     * 解析 Manifest 文件
     *
     * @return { "com.xxx.SampleActivity": "@style/activity_transparent" }
     */
    private fun File.parseManifest(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this).apply {
            val manifestNode = getElementsByTagName("manifest").item(0) as Element
            val packageName = manifestNode.getAttribute("package")

            val activityNodes = getElementsByTagName("activity")
            for (i in 0 until activityNodes.length) {
                val node = activityNodes.item(i) as Element
                val activityName = node.getAttribute("android:name").let {
                    if (!it.startsWith(".")) {
                        it
                    } else {
                        "$packageName$it"
                    }
                }
                if (node.hasAttribute(KEY_ATTRIBUTE_ORIENTATION)
                    && node.getAttribute(KEY_ATTRIBUTE_ORIENTATION) != "unspecified"
                ) {
                    if (node.hasAttribute("android:theme")) {
                        result[activityName] = node.getAttribute("android:theme")
                    }
                }
            }
        }
        return result
    }


    /**
     * 获取透明主题列表
     */
    private fun ApplicationVariantImpl.getTranslucentStyleList(): List<String> {
        return getArtifactFiles(AndroidArtifacts.ArtifactType.AAR).flatMap {
            ZipFile(it).use { zipFile ->
                zipFile.entries().toList().filterNot(ZipEntry::isDirectory).firstOrNull { entry ->
                    entry.name == "res/values/values.xml"
                }?.let { e ->
                    zipFile.getInputStream(e).parseValueXml()
                }?: emptyList()
            }
        }
    }

    /**
     * 解析 values.xml
     */
    private fun InputStream.parseValueXml(): List<String> {
        val result = mutableListOf<String>()
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this).apply {
            val styleNodes = getElementsByTagName("style")
            for (i in 0 until styleNodes.length) {
                val styleNode = styleNodes.item(i) as Element
                val isTransparent = styleNode.getElementsByTagName("item").toList()
                    .asSequence()
                    .map { it.attributes.getNamedItem("name")?.nodeValue to it.textContent }
                    .find {
                        (it.first == "android:windowIsTranslucent" && it.second == "true")
                                || (it.first == "android:windowIsFloating" && it.second == "true")
                    } != null

                if (isTransparent) {
                    result.add(styleNode.getAttribute("name"))
                }
            }
        }
        return result
    }

}

private const val KEY_ATTRIBUTE_ORIENTATION = "android:screenOrientation"