package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.internal.aar.AarAnalyse
import com.omooo.plugin.reporter.HtmlReporter
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.util.*
import com.omooo.plugin.util.getArtifactIdFromAarName
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.green
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Author: Omooo
 * Date: 2023/07/25
 * Desc: Aar 分析任务
 * Use: ./gradlew aarAnalyse
 * Output: projectDir/aarAnalyse.html
 */
internal open class AarAnalyseTask : DefaultTask() {
    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun doAction() {
        println(
            """
                *********************************************
                ********** -- AarAnalyseTask -- *************
                ***** -- projectDir/aarAnalyse.html -- ******
                *********************************************
            """.trimIndent()
        )
        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        val startTime = System.currentTimeMillis()
        val ownerShip = project.getOwnerShip()
        val aarList =
            (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.AAR_OR_JAR
            ).artifacts.map { artifact ->
                AarFile(
                    name = artifact.getArtifactName().removeVersionFromAarName(),
                    size = File(artifact.file.absolutePath).length(),
                    owner = ownerShip.getOrDefault(
                        artifact.getArtifactName().getArtifactIdFromAarName(), "unknown"
                    ),
                )
            }
        val reporter = AarAnalyse(project).analyse(
            variant.applicationId,
            Pair((variant as ApplicationVariantImpl).versionName, aarList.sortedByDescending { it.size })
        )
        HtmlReporter().generateAarAnalyseReport(reporter, "${project.parent?.projectDir}/aarAnalyse.html")

        println(green("Spend time: ${System.currentTimeMillis() - startTime}ms"))
    }

}