package com.omooo.plugin.util

import org.gradle.api.Project
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

private const val DIR_PLUGIN_FILES = "lavender-plugin"
private const val FILE_OWNERSHIP = "ownership.yaml"