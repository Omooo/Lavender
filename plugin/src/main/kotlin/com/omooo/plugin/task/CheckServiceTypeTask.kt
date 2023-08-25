package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.HtmlReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.getOwner
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.attributeMap
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.toList
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Author: Omooo
 * Date: 2023/08/25
 * Desc: 检测 <service /> 组件是否设置了 android:foregroundServiceType 属性（Android 14 强制需要包含该属性）
 * Use: ./gradlew checkServiceType
 * Output: projectDir/checkServiceType.json
 */
internal open class CheckServiceTypeTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ********* -- CheckServiceTypeTask -- ********
                *** -- projectDir/checkServiceType.json -- **
                *********************************************
            """.trimIndent()
        )
        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        val ownerShipMap = project.getOwnerShip()
        val appProjectResult =
            project.name to project.projectDir.resolve("src/main/AndroidManifest.xml")
                .getComponentList()
        (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.MANIFEST
        ).artifacts.associate { artifact ->
            artifact.getArtifactName() to artifact.file.getComponentList()
        }.plus(appProjectResult).filter {
            it.value.isNotEmpty()
        }.also {
            val reporter = AppReporter(
                desc = Insight.Title.CHECK_SERVICE_TYPE,
                documentLink = Insight.DocumentLink.CHECK_SERVICE_TYPE,
                versionName = (variant as ApplicationVariantImpl).versionName,
                variantName = variant.name,
                aarList = it.toList().map {(aarName, serviceNameList)->
                    AarFile(
                        name = aarName,
                        size = 0,
                        owner = ownerShipMap.getOwner(aarName),
                        fileList = serviceNameList.map { s -> AppFile(s) }.toMutableList()
                    )
                }
            )
            HtmlReporter().generateReport(reporter, "${project.parent?.projectDir}/checkServiceType.html")
        }
    }

    /**
     * 获取检测的组件列表
     *
     * 入参: Manifest 文件
     */
    private fun File.getComponentList(): List<String> {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(this).let { document ->
                val manifestNode = document.getElementsByTagName("manifest").item(0) as? Element
                val packageName = manifestNode?.getAttribute("package").orEmpty()
                document.getElementsByTagName("service").toList()
                    .filterNot { serviceNode ->
                        serviceNode.attributeMap().containsKey("android:foregroundServiceType")
                    }.map { node ->
                        node.attributeMap().getOrDefault("android:name", "unknown").let {
                            if (it.startsWith(packageName) || !it.startsWith(".")) {
                                it
                            } else {
                                "$packageName$it"
                            }
                        }
                    }
            }
    }

}