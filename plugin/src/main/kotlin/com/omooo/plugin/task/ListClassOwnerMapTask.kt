package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.util.*
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Author: Omooo
 * Date: 2023/3/15
 * Desc: 输出类的归属者映射
 * Use: ./gradlew listClassOwnerMap
 * Output: projectDir/classOwnerMap.json
 */
internal open class ListClassOwnerMapTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

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

        if (variant !is ApplicationVariantImpl) {
            println(red("${variant.name} is not an application variant."))
            return
        }

        val ownerShip = project.getOwnerShip()
        (variant as ApplicationVariantImpl).getArtifactClassMap().mapValues {
            ownerShip.getOrDefault(it.value.first.getArtifactIdFromAarName(), "unknown")
        }.filterValues {
            it != "unknown"
        }.writeToJson("${project.parent?.projectDir}/classOwnerMap.json")

    }
}