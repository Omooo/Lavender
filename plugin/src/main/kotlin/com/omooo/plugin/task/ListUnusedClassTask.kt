package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.internal.cha.ComponentHandler
import com.omooo.plugin.internal.cha.LayoutHandler
import com.omooo.plugin.util.*
import com.omooo.plugin.util.getOwnerShip
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode

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
        val v = variant as ApplicationVariantImpl

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

        val printUnusedClass = project.hasProperty("printUnusedClass")
        val ownerShip = project.getOwnerShip()
        val classMap = v.getArtifactClassMap()

        val classNodeCache = v.getAllClasses()
        val entryPoint = v.getEntryPoint()
        val tempAllClasses = if (printUnusedClass) mutableListOf(classMap.keys) else mutableListOf()

    }

    /**
     * 获取入口类
     */
    private fun ApplicationVariantImpl.getEntryPoint(): Set<String> {
        // 自定义 View 入口
        val viewEntryPoint = getArtifactFiles(AndroidArtifacts.ArtifactType.ANDROID_RES)
            .plus(project.projectDir.resolve("src/main/res"))
            .flatMap {
                it.walk().filter { resDir ->
                    resDir.isDirectory && resDir.name.startsWith("layout")
                }.map { layoutDir ->
                    layoutDir.getAllChildren()
                }
            }.asSequence().flatten().map {
                LayoutHandler(it).getViews()
            }.flatten().filter {
                it.startsWith("com.omooo")
            }
        // Manifest 里四大组件入口
        val componentEntryPoint = getArtifactFiles(AndroidArtifacts.ArtifactType.MANIFEST)
            .plus(project.projectDir.resolve("src/main/AndroidManifest.xml"))
            .map {
                ComponentHandler(it).getComponentSet()
            }.flatten()
        return viewEntryPoint.plus(componentEntryPoint).toSet()
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