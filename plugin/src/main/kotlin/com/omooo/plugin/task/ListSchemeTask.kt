package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.util.*
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Author: Omooo
 * Date: 2023/3/16
 * Desc: 输出 Manifest 里定义的 scheme 集合
 * Use: ./gradlew listSchemes
 * Output: projectDir/schemes.json
 */
internal open class ListSchemeTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ********** -- ListSchemeTask -- *************
                ******* -- projectDir/schemes.json -- *******
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println(red("${variant.name} is not an application variant."))
            return
        }

        (variant as ApplicationVariantImpl).getArtifactFiles(AndroidArtifacts.ArtifactType.MANIFEST)
            .map {
                it.parseManifest()
            }.filter {
                it.isNotEmpty()
            }.flatMap {
                it.entries
            }.associate {
                it.toPair()
            }.writeToJson("${project.parent?.projectDir}/schemes.json")
    }

    /**
     * 解析 Manifest 文件
     *
     * @return { "com.xxx.SampleActivity": "scheme1://xxx/xx, scheme2://xxx/xx" }
     */
    private fun File.parseManifest(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this).apply {
            val manifestNode = getElementsByTagName("manifest").item(0) as Element
            val packageName = manifestNode.getAttribute("package")

            val activityNodes = getElementsByTagName("activity")
            for (i in 0 until activityNodes.length) {
                val activityNode = activityNodes.item(i) as Element
                val activityName = "${packageName}${activityNode.getAttribute("android:name")}"

                val dataNodes = activityNode.getElementsByTagName("data")
                if (dataNodes.length > 0) {
                    var scheme = ""
                    for (j in 0 until dataNodes.length) {
                        val dataNode = dataNodes.item(j) as Element
                        val dataValue = dataNode.getAttribute("android:scheme") + "://" +
                                dataNode.getAttribute("android:host") + dataNode.getAttribute("android:path")
                        scheme = if (scheme.isEmpty()) dataValue else "$scheme, $dataValue"
                    }
                    result[activityName] = scheme
                }
            }
        }
        return result
    }

}