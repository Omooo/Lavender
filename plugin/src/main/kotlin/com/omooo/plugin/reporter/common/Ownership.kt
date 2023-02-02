package com.omooo.plugin.reporter.common

import org.yaml.snakeyaml.Yaml

/**
 * Author: Omooo
 * Date: 2023/2/2
 * Desc: 归属人信息
 */
internal class Ownership {
    /**
     * 获取归属人映射关系
     *
     * @return Map<AAR Name, Owner Name>
     */
    fun getOwnerMap(): Map<String, String> {
        val yaml = Yaml()
        val entries: Map<String, List<String>> =
            javaClass.getResourceAsStream("/ownership.yaml").use(yaml::load)
        return entries.flatMap { entry ->
            entry.value.map {
                it to entry.key
            }
        }.toMap()
    }
}