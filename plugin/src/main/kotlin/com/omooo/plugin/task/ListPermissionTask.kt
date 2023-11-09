package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.util.getArtifactName
import com.omooo.plugin.util.writeToJson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.util.regex.Pattern

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: 输出 app 及其依赖的 aar 权限信息
 * Use: ./gradlew listPermissions
 * Output: projectDir/permissions.json
 */
internal open class ListPermissionTask : DefaultTask() {

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun doAction() {
        println(
            """
                *********************************************
                ********* -- ListPermissionTask -- **********
                ***** -- projectDir/permissions.json -- *****
                *********************************************
            """.trimIndent()
        )

        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        val resultMap = HashMap<String, List<String>>()
        // 获取 app 模块的权限
        getAppModulePermission(resultMap)
        // 获取 app 依赖的 aar 权限
        (variant as ApplicationVariantImpl).variantData.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.MANIFEST
        ).artifacts.asSequence().filter { artifact ->
            !resultMap.containsKey(artifact.getArtifactName())
                    && matchPermission(artifact.file.readText()).isNotEmpty()
        }.forEach {
            resultMap[it.getArtifactName()] = matchPermission(it.file.readText())
        }.also {
            resultMap.writeToJson("${project.parent?.projectDir}/permissions.json")
        }
        if (project.hasProperty("simpleStyle")) {
            resultMap.values.flatten().toSet().sorted().apply {
                writeToJson("${project.parent?.projectDir}/permissionSet.json")
            }
        }
    }

    /**
     * 获取 app 模块的权限信息
     */
    private fun getAppModulePermission(map: HashMap<String, List<String>>) {
        val file = project.projectDir.resolve("src/main/AndroidManifest.xml")
        if (file.exists()) {
            map["app"] = matchPermission(file.readText())
        } else {
            println("App manifest is missing for path ${file.absolutePath}")
        }
    }

    /**
     * 根据 Manifest 文件匹配权限信息
     */
    private fun matchPermission(text: String): List<String> {
        val list = ArrayList<String>()
        val pattern = Pattern.compile("<uses-permission.+./>")
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            list.add(matcher.group())
        }
        return list
    }

}