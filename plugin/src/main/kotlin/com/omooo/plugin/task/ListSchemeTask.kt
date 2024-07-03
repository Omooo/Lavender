package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.util.*
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Author: Omooo
 * Date: 2023/3/16
 * Desc: 输出 Manifest 里定义的 scheme 集合
 * Use: ./gradlew listSchemes
 * Output: projectDir/schemes.json
 */
internal abstract class ListSchemeTask : DefaultTask() {

    @get:Internal
    lateinit var variant: Variant
    @get:InputFiles
    abstract var apkFileCollection: FileCollection

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

        val startTime = System.currentTimeMillis()
        val ownerShip = project.getOwnerShip()
        val classOwnerMap = variant.getArtifactClassMap().mapValues {
            ownerShip.getOwner(it.value.first)
        }
        variant.getArtifactFiles(AndroidArtifacts.ArtifactType.MANIFEST)
            .map {
                it.parseSchemesFromManifest()
            }.filter {
                it.isNotEmpty()
            }.flatMap {
                it.entries
            }.associate {
                it.toPair()
            }.mapValues {
                Pair(classOwnerMap.getOrDefault(it.key, "unknown"), it.value)
            }.toSortedMap().writeToJson("${project.parent?.projectDir}/schemes.json")
        println(
            green("ListSchemeTask execute success in ${System.currentTimeMillis() - startTime}ms")
        )
    }

}