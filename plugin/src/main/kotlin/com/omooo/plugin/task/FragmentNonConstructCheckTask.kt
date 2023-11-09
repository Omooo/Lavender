package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.bean.ASM_VERSION
import com.omooo.plugin.reporter.AppReporter
import com.omooo.plugin.reporter.HtmlReporter
import com.omooo.plugin.reporter.Insight
import com.omooo.plugin.reporter.common.AarFile
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.green
import com.omooo.plugin.util.getAllChildren
import com.omooo.plugin.util.getArtifactClassMap
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.getOwner
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.isInternalComponent
import com.omooo.plugin.util.red
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Author: Omooo
 * Date: 2023/11/8
 * Desc: Fragment 无参构造方法检查
 * Use: ./gradlew checkFragmentNonConstruct
 * Output: projectDir/fragmentNonConstruct.html
 */
internal open class FragmentNonConstructCheckTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                **** -- FragmentNonConstructCheckTask -- ****
                **-- projectDir/fragmentNonConstruct.html --* 
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println(red("${variant.name} is not an application variant."))
            return
        }

        val classNodeMap =
            (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.CLASSES
            ).artifacts.filter {
                project.isInternalComponent(it.getArtifactName())
            }.flatMap {
                it.file.parseClassNode()
            }.associateBy { it.name }

        val ownerShip = project.getOwnerShip()
        val classOwnerMap = (variant as ApplicationVariantImpl).getArtifactClassMap()

        val resultList = classNodeMap.check().map { it.replace("/", ".") }
        if (resultList.isEmpty()) {
            println(green("FragmentNonConstructCheckTask execute success, result is empty."))
            return
        }
        println(red("FragmentNonConstructCheckTask execute success, size: ${resultList.size}."))
        val aarList = resultList.groupBy {
            classOwnerMap[it]?.first ?: "unknown"
        }.map {
            AarFile(
                name = it.key,
                size = 0,
                owner = ownerShip.getOwner(it.key),
                fileList = it.value.map { AppFile(name = it) }.toMutableList()
            )
        }
        AppReporter(
            desc = Insight.Title.CHECK_FRAGMENT_CONSTRUCT,
            documentLink = Insight.DocumentLink.CHECK_FRAGMENT_CONSTRUCT,
            versionName = (variant as ApplicationVariantImpl).versionName,
            variantName = variant.name,
            aarList = aarList,
        ).apply {
            HtmlReporter().generateReport(this, "${project.parent?.projectDir}/fragmentNonConstruct.html")
        }
    }

    /**
     * 检查
     *
     * @return 返回无参构造方法的类
     */
    private fun Map<String, ClassNode>.check() :List<String>{
        val result = mutableListOf<String>()
        val classNodeList = this.map { it.value }
        classNodeList.filter {
            it.extendFragment(this)
        }.forEach { classNode ->
            classNode.methods.find {
                // 忽略方法可见性的检查
                it.name == "<init>" && it.desc == "()V"
            } ?: run {
                // 坏了，没找到无参的构造方法
                result.add(classNode.name)
            }
        }
        return result
    }

    /**
     * 是否继承 Fragment 相关类
     */
    private fun ClassNode.extendFragment(classNodeMap: Map<String, ClassNode>): Boolean {
        if (superName in listOf(
                "com/google/android/material/bottomsheet/BottomSheetDialogFragment",
                "androidx/fragment/app/DialogFragment",
                "androidx/fragment/app/Fragment",
            )
        ) {
            return true
        }
        return classNodeMap[superName]?.extendFragment(classNodeMap) ?: false
    }

    /**
     * 解析成 [ClassNode]
     */
    @Suppress("NestedBlockDepth")
    private fun File.parseClassNode(): List<ClassNode> {
        val result: MutableList<ClassNode> = mutableListOf()
        if (isDirectory) {
            getAllChildren().filter {
                it.extension == "class"
            }.forEach {
                val classNode = ClassNode(ASM_VERSION)
                ClassReader(it.readBytes()).accept(
                    classNode, ClassReader.SKIP_DEBUG
                )
                result.add(classNode)
            }
        } else {
            ZipFile(this).use { zipFile ->
                zipFile.entries().toList().filterNot(ZipEntry::isDirectory).forEach { entry ->
                    if (entry.name.endsWith(".class")) {
                        val classNode = ClassNode(ASM_VERSION)
                        ClassReader(zipFile.getInputStream(entry)).accept(
                            classNode, ClassReader.SKIP_DEBUG
                        )
                        result.add(classNode)
                    }
                }
            }
        }
        return result
    }
}