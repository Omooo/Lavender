package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.util.getArtifactClassMap
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Author: Omooo
 * Date: 2023/5/24
 * Desc: 输出业务模块的包名列表（用于 Robust 配置）
 * Use: ./gradlew listPackageName
 * Output: projectDir/packageNameList.xml
 */
@Suppress("DEPRECATION")
internal open class ListPackageNameTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun run() {
        println(
            """
                *********************************************
                ********* -- ListPackageNameTask -- *********
                **** -- projectDir/packageNameList.xml -- ***
                *********************************************
            """.trimIndent()
        )
        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        (variant as ApplicationVariantImpl).getArtifactClassMap().keys.map {
            it.getPackageNameFromClassName()
        }.toSet().writeXml("${project.parent?.projectDir}/packageNameList.xml")
    }

    /**
     * 从类名中获取指定段数的包名（默认三段）
     *
     * "androidx.core.graphics.PaintKt"
     *  n=3: "androidx.core.graphics"
     *  n=2: "androidx.core"
     */
    private fun String.getPackageNameFromClassName(): String {
        val l = if (project.hasProperty("length"))
            project.properties["length"].toString().toInt() else DEFAULT_PACKAGE_LENGTH
        val segments = this.split(".")
        val endIndex = segments.size.coerceAtMost(l)
        return segments.subList(0, endIndex).joinToString(".")
    }


    /**
     * 写入 xml
     *
     * @param filePath 文件路径
     */
    private fun Set<String>.writeXml(filePath: String) {
        val text = buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("<resources>")
            this@writeXml.forEach { s ->
                appendLine("    <name>$s</name>")
            }

            append("</resources>")
        }
        File(filePath).apply {
            if (exists()) {
                delete()
            }
            createNewFile()
            writeText(text)
        }
    }

}

private const val DEFAULT_PACKAGE_LENGTH = 3