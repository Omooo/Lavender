package com.omooo.plugin.util

import org.w3c.dom.Node
import org.w3c.dom.NodeList

/**
 * Author: Omooo
 * Date: 2022/12/20
 * Desc: Xml 解析相关扩展函数
 */

/**
 * NodeList 转 List
 */
internal fun NodeList.toList(): List<Node> {
    val list = mutableListOf<Node>()
    for (i in 0 until this.length) {
        list.add(item(i))
    }
    return list
}

/**
 * Node 属性转 Map
 */
internal fun Node.attributeMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    for (i in 0 until this.attributes.length) {
        map[attributes.item(i).nodeName] = attributes.item(i).nodeValue
    }
    return map
}