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
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.getOwner
import com.omooo.plugin.util.getOwnerShip
import com.omooo.plugin.util.isInternalComponent
import com.omooo.plugin.util.parseClassNode
import com.omooo.plugin.util.red
import com.omooo.plugin.util.variantImpl
import com.omooo.plugin.util.versionName
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.tree.ClassNode

/**
 * Author: Omooo
 * Date: 2023/11/8
 * Desc: Fragment 无参构造方法检查
 * Use: ./gradlew checkFragmentNonConstruct
 * Output: projectDir/fragmentNonConstruct.html
 */
internal abstract class FragmentNonConstructCheckTask : DefaultTask() {

    @get:Internal
    lateinit var variant: Variant

    @get:InputFiles
    abstract var apkFileCollection: FileCollection

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

        val classNodeMap =
            variant.variantImpl.variantDependencies.getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.CLASSES
            ).artifacts.filter {
                project.isInternalComponent(it.getArtifactName())
            }.flatMap {
                it.file.parseClassNode()
            }.associateBy { it.name }

        val ownerShip = project.getOwnerShip()
        val classOwnerMap = variant.getArtifactClassMap()

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
            versionName = variant.versionName,
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

}