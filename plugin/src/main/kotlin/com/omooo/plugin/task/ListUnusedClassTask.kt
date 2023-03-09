package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.*
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Author: Omooo
 * Date: 2023/3/8
 * Desc: 输出无用类
 * Use: ./gradlew listUnusedClass
 * Output: projectDir/listUnusedClass.json
 */
internal open class ListUnusedClassTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ********* -- ListUnusedClassTask -- *********
                *** -- projectDir/listUnusedClass.json -- ***
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }

        val unusedClassList = project.getUnusedClass()
        if (unusedClassList.isEmpty()) {
            println(
                """
                Unused class is empty.
                Please check '${project.buildDir.resolve("outputs/mapping/${variant.name}/usage.txt")}' file.
            """.trimIndent()
            )
            return
        }

        val ownerShip = project.getOwnerShip()
        val classMap = (variant as ApplicationVariantImpl).getClassMap()
        classMap.keys

        val aarMap = mutableMapOf<String, AarFile>()
        unusedClassList.forEach { className ->
            val filePair = classMap.getOrDefault(className, Pair("unknown", 0))
            val owner = ownerShip.getOrDefault(filePair.first.getArtifactIdFromAarName(), "unknown")
            if (!aarMap.containsKey(filePair.first)) {
                aarMap[filePair.first] = AarFile(filePair.first, 0, owner, mutableListOf())
            }
            aarMap[filePair.first]!!.fileList.add(
                AppFile(name = className.replace("$", "/"), size = filePair.second)
            )
            aarMap[filePair.first]!!.size += filePair.second
        }

        AppReporter(
            desc = Insight.Title.LIST_UNUSED_CLASS,
            documentLink = Insight.DocumentLink.LIST_UNUSED_CLASS,
            versionName = (variant as ApplicationVariantImpl).versionName,
            variantName = variant.name,
            aarList = aarMap.values.toList(),
        ).apply {
            writeToJson("${project.parent?.projectDir}/listUnusedClass.json")
            println("Unused class count: ${aarList.sumOf { it.fileList.size }}, total size: ${aarList.sumOf { it.size } / 1024}kb ")
        }
    }

    /**
     * 从 usage.txt 文件中匹配无用类
     *
     * @return ag: {"com.nio.UnusedClassName", "xxx"}
     */
    private fun Project.getUnusedClass(): List<String> {
        val file = project.buildDir.resolve("outputs/mapping/${variant.name}/usage.txt")
        if (!file.exists() || file.readText().isEmpty()) {
            return emptyList()
        }
        return file.readLines().filterNot {
            it.startsWith("  ") || it.endsWith(":")
        }.filterNot {
            // 过滤掉 ViewBinding 类: com.xxx.xx.databinding.XxxBinding
            it.endsWith("Binding") && it.substringBeforeLast(".").endsWith("databinding")
        }.filterNot {
            // kt 文件中包含顶层函数或属性时，编译器将会为该文件生成一个相应的 “XxxKt” 文件
            it.endsWith("Kt")
        }
    }
}