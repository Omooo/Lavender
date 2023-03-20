package com.omooo.plugin.task

import com.android.SdkConstants
import com.android.build.api.artifact.SingleArtifact
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.internal.apk.ApkParser
import com.omooo.plugin.internal.apk.clear
import com.omooo.plugin.util.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
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
        getDependencies()
        val apkFile =
            Files.walk(variant.component.artifacts.get(SingleArtifact.APK).get().asFile.toPath())
                .filter {
                    it.extension == SdkConstants.EXT_ANDROID_PACKAGE
                }.findFirst().get().toFile()
        val appFileList = ApkParser().parse(apkFile).clear(variant)
        appFileList.writeToJson("${project.parent?.projectDir}/app-clean.json")
    }

    private fun getDependencies() {
        (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.AAR
        ).artifacts.map { artifact ->
            artifact.getArtifactName() to artifact.file.parseJar()
        }.writeToJson("${project.parent?.projectDir}/app-getDependencies.json")
    }
}