package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.omooo.plugin.util.*
import com.omooo.plugin.util.attributeMap
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.toList
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Author: Omooo
 * Date: 2022/12/20
 * Desc: 检测声明了 <intent-filter> 的组件是否包含 android:exported 属性（Android 12 强制需要包含该属性）
 * Use: ./gradlew checkExported
 * Output: projectDir/checkExported.json
 */
internal abstract class CheckExportedTask : DefaultTask() {

    @get:Internal
    lateinit var variant: Variant

    @get:Internal
    abstract val manifests: Property<ArtifactCollection>

    @get:InputFile
    abstract val mainManifest: Property<File>

//    @get:InputFile
//    abstract val mergedManifest: RegularFileProperty

    /** 检测的节点列表 */
    private val checkNodeList = listOf("activity", "service", "receiver")

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ********** -- CheckExportedTask -- **********
                **** -- projectDir/checkExported.json -- ****
                *********************************************
            """.trimIndent()
        )
        val appProjectResult = project.name to mainManifest.get().getComponentList()
        manifests.get().artifacts.associate { artifact ->
            artifact.getArtifactName() to artifact.file.getComponentList()
        }.plus(appProjectResult).filter {
            it.value.isNotEmpty()
        }.also {
            it.writeToJson("${project.parent?.projectDir}/checkExported.json")
        }
    }

    /**
     * 获取检测的组件列表
     *
     * 入参: Manifest 文件
     */
    private fun File.getComponentList(): List<String> {
        return checkNodeList.asSequence().map {
            DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(this).getElementsByTagName(it).toList()
        }.flatten().filter {
            it.childNodes.toList().map { it.nodeName }.contains("intent-filter")
        }.filterNot {
            it.attributeMap().containsKey("android:exported")
        }.map {
            it.attributeMap().getOrDefault("android:name", "unknown")
        }.toList()
    }

}