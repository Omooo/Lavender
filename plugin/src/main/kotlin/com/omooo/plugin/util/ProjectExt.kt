package com.omooo.plugin.util

import com.android.build.api.variant.Variant
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.yaml.snakeyaml.Yaml

/**
 * Author: Omooo
 * Date: 2023/2/10
 * Desc: [Project] 相关扩展函数
 */

/**
 * 获取归属人映射关系
 *
 * @return Map<AAR Artifact Name, Owner Name>x
 */
internal fun Project.getOwnerShip(): Map<String, String> {
    val ownershipFile = parent?.projectDir?.resolve("$DIR_PLUGIN_FILES/$FILE_OWNERSHIP")
    if (ownershipFile?.exists() == true) {
        return Yaml().load<Map<String, List<String>>>(ownershipFile.readText()).entries.flatMap { entry ->
            entry.value.map {
                it to entry.key
            }
        }.toMap()
    }
    return emptyMap()
}

/**
 * 获取归属人映射关系
 *
 * @return Map<Owner Name, List<AAR Name>>x
 */
internal fun Project.getOwnerMap(): Map<String, List<String>> {
    val ownershipFile = parent?.projectDir?.resolve("$DIR_PLUGIN_FILES/$FILE_OWNERSHIP")
    if (ownershipFile?.exists() == true) {
        return Yaml().load(ownershipFile.readText())
    }
    return emptyMap()
}

/**
 * 根据 AAR 的 groupId 前缀匹配出 owner
 */
internal fun Map<String, String>.getOwner(aarName: String): String {
    return this.entries.find {
        aarName.startsWith(it.key)
    }?.value ?: "unknown"
}

/**
 * 是否是内部组件
 */
internal fun Project.isInternalComponent(aarName: String): Boolean {
    return getOwnerMap().flatMap { it.value }.find {
        aarName.startsWith(it)
    } != null
}

internal fun Project.getJarTaskProviders(variant: Variant): List<TaskProvider<out Task>>  {
    return emptyList()
}

internal val Project.isAndroid: Boolean
    get() = plugins.hasPlugin("com.android.application")
            || plugins.hasPlugin("com.android.dynamic-feature")
            || plugins.hasPlugin("com.android.library")

internal val Project.isJava: Boolean
    get() = plugins.hasPlugin("java") || isJavaLibrary

internal val Project.isJavaLibrary: Boolean
    get() = plugins.hasPlugin("java-library")

/** 是否需要输出报告 */
internal val Project.needPrintReporter: Boolean
    get() = hasProperty("printReporter")


private const val DIR_PLUGIN_FILES = "lavender-plugin"
private const val FILE_OWNERSHIP = "ownership.yaml"