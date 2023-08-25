package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.getGroupIdFromAarName
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Author: Omooo
 * Date: 2022/11/13
 * Desc: 统计依赖的 AAR 大小
 * Use: ./gradlew listAarSize
 * Output: projectDir/listAarSize.json
 */
internal open class ListAarSizeTask : DefaultTask() {
    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun doAction() {
        println(
            """
                *********************************************
                ********** -- ListAarSizeTask -- ************
                ***** -- projectDir/listAarSize.json -- *****
                *********************************************
            """.trimIndent()
        )
        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        var resultMap = mutableMapOf<String, Long>()
        (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.AAR_OR_JAR
        ).artifacts.forEach { artifact ->
            val size = File(artifact.file.absolutePath).length() / 1024
            resultMap[artifact.getArtifactName()] = size
        }

        // 配置了 group-by 参数
        if (project.hasProperty("sortByGroupId")) {
            resultMap = resultMap
                .toList()
                .groupBy({ it.first.getGroupIdFromAarName() }) { it.second }
                .mapValues { (_, values) -> values.sum() }
                .toMutableMap()
        }

        resultMap.toList().asSequence().filter {
            if (project.hasProperty("groupIdPrefix")) {
                it.first.getGroupIdFromAarName().startsWith(project.properties["groupIdPrefix"].toString())
            } else {
                true
            }
        }.sortedByDescending { (_, value) ->
            value
        }.toMap().mapValues {
            "${it.value}kb"
        }.also {
            it.writeToJson("${project.parent?.projectDir}/listAarSize.json")
        }
    }

}