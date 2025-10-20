package com.omooo.plugin.task

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.HtmlReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.green
import com.omooo.plugin.util.getArtifactClassMap
import com.omooo.plugin.util.getArtifactIdFromAarName
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.isGoodsDetailComponent
import com.omooo.plugin.util.parseClassNode
import com.omooo.plugin.util.red
import com.omooo.plugin.util.variantImpl
import com.omooo.plugin.util.versionName
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.TypeInsnNode

/**
 * Author: Omooo
 * Date: 2025/10/19
 * Desc: checkcast 检查，避免强转导致的运行时崩溃
 * Use: ./gradlew checkCast
 * Output: projectDir/checkCast.html
 */
internal abstract class CheckCastTask : DefaultTask() {

    @get:Internal
    lateinit var variant: Variant

    @get:InputFiles
    abstract var apkFileCollection: FileCollection

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ************ -- CheckCastTask -- ************
                *******-- projectDir/checkCast.html --*******
                *********************************************
            """.trimIndent()
        )

        val classNodeMap =
            variant.variantImpl.variantDependencies.getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.CLASSES
            ).artifacts.filter {
                it.getArtifactName().getArtifactIdFromAarName().isGoodsDetailComponent()
            }.flatMap {
                it.file.parseClassNode()
            }.associateBy { it.name }
        val classOwnerMap = variant.getArtifactClassMap()

        val resultList = classNodeMap.check().map { it.replace("/", ".") }
        if (resultList.isEmpty()) {
            println(green("CheckCastTask execute success, result is empty."))
            return
        }
        println(red("CheckCastTask execute success, size: ${resultList.size}."))
        val aarList = resultList.groupBy {
            classOwnerMap[it]?.first ?: "unknown"
        }.map {
            AarFile(
                name = it.key,
                size = 0,
                owner = "unknown",
                fileList = it.value.map { AppFile(name = it) }.toMutableList()
            )
        }
        AppReporter(
            desc = Insight.Title.CHECK_CAST,
            documentLink = Insight.DocumentLink.CHECK_CAST,
            versionName = variant.versionName,
            variantName = variant.name,
            aarList = aarList,
        ).apply {
            HtmlReporter().generateReport(this, "${project.parent?.projectDir}/checkCast.html")
        }
    }

    /**
     * 判断存在 checkcast 指令但是没有 instanceof 判断
     */
    private fun Map<String, ClassNode>.check(): Set<String> {
        val result = mutableSetOf<String>()
        this.values.forEach { classNode ->
            classNode.methods.forEach { methodNode ->
                val instructions = methodNode.instructions.toArray()
                instructions.forEachIndexed { index, insn ->
                    if (insn is TypeInsnNode && insn.opcode == Opcodes.CHECKCAST && insn.desc.isActivityType()) {
                        var endIndex = index - 1
                        var hasInstanceOf = false
                        while (endIndex >= 0) {
                            val insnNode = instructions.getOrNull(endIndex)
                            if (insnNode is TypeInsnNode && insnNode.opcode == Opcodes.INSTANCEOF && insnNode.desc == insn.desc) {
                                // 存在 instanceof 判断
                                hasInstanceOf = true
                                break
                            }
                            endIndex--
                        }
                        if (!hasInstanceOf) {
                            result.add("${classNode.name.substringAfterLast("/")}.${methodNode.name}")
                        }
                    }
                }
            }
        }
        result.forEach {
            println(green(it))
        }
        return result
    }

    /**
     * 检查所有可能的 Activity 类型转换
     */
    private fun String.isActivityType(): Boolean {
        val activityPatterns = listOf(
            // 精确匹配已知的 Activity 类型
            "android/app/Activity",
            "android/app/.*Activity",  // 匹配所有 android.app 包下的 Activity
            "androidx/.*/.*Activity",  // 匹配所有 androidx 包下的 Activity
            "android/support/.*/.*Activity", // 匹配所有 support 包下的 Activity
            ".*Activity$"  // 匹配任何以 Activity 结尾的类
        )

        return activityPatterns.any { pattern ->
            this.matches(Regex(pattern))
        }
    }
}