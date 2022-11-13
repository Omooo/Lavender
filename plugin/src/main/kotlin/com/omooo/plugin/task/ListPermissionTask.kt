package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.tasks.CheckManifest
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

        val map = HashMap<String, List<String>>()

        // 获取 app 模块的权限
        val checkManifestTask = variant.checkManifestProvider.get() as CheckManifest
        if (checkManifestTask.isManifestRequiredButNotPresent()) {
            println("App manifest is missing for variant ${variant.name}")
            getAppModulePermission(map)
        } else {
            val manifest = checkManifestTask.fakeOutputDir.asFile.get()
            if (manifest.exists()) {
                map["app"] = matchPermission(manifest.readText())
            } else {
                getAppModulePermission(map)
            }
        }

        // 获取 app 依赖的 aar 权限
        val variantData = (variant as ApplicationVariantImpl).variantData
        val manifests = variantData.variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.ALL,
            AndroidArtifacts.ArtifactType.MANIFEST
        )
        val artifacts = manifests.artifacts
        for (artifact in artifacts) {
            if (!map.containsKey(artifact.getArtifactName())
                && matchPermission(artifact.file.readText()).isNotEmpty()
            ) {
                map[artifact.getArtifactName()] = matchPermission(artifact.file.readText())
            }
        }

        map.writeToJson("${project.parent?.projectDir}/permissions.json")
    }

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