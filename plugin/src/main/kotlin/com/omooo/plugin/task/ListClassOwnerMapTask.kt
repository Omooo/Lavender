package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.omooo.plugin.util.*
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Author: Omooo
 * Date: 2023/3/15
 * Desc: 输出类的归属者映射
 * Use: ./gradlew listClassOwnerMap
 * Output: projectDir/classOwnerMap.json
 */
internal abstract class ListClassOwnerMapTask : DefaultTask() {

    @get:Internal
    lateinit var variant: Variant
    @get:InputFiles
    abstract var apkFileCollection: FileCollection

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ******* -- ListClassOwnerMapTask -- *********
                **** -- projectDir/classOwnerMap.json -- ****
                *********************************************
            """.trimIndent()
        )

        val ownerShip = project.getOwnerShip()
        variant.getArtifactClassMap().mapValues {
            ownerShip.getOrDefault(it.value.first.getArtifactIdFromAarName(), "unknown")
        }.filterValues {
            it != "unknown"
        }.writeToJson("${project.parent?.projectDir}/classOwnerMap.json")

    }
}