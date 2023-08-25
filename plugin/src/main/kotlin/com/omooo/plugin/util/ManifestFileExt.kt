package com.omooo.plugin.util

import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Author: Omooo
 * Date: 2023/8/21
 * Desc: Manifest 文件解析相关扩展方法
 */

/**
 * 解析 Manifest 文件
 *
 * @return { "com.xxx.SampleActivity": "scheme://home/mall, scheme://home/mine" }
 */
@Suppress("NestedBlockDepth")
internal fun File.parseSchemesFromManifest(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this).apply {
        val manifestNode = getElementsByTagName("manifest").item(0) as Element
        val packageName = manifestNode.getAttribute("package")

        val activityNodes = getElementsByTagName("activity")
        for (i in 0 until activityNodes.length) {
            val activityNode = activityNodes.item(i) as Element
            val activityName = activityNode.getAttribute("android:name").let {
                if (it.startsWith(packageName) || !it.startsWith(".")) {
                    it
                } else {
                    "$packageName$it"
                }
            }

            val dataNodes = activityNode.getElementsByTagName("data")
            if (dataNodes.length > 0) {
                var scheme = ""
                for (j in 0 until dataNodes.length) {
                    val dataNode = dataNodes.item(j) as Element
                    val dataValue = dataNode.getAttribute("android:scheme") + "://" +
                            dataNode.getAttribute("android:host") + dataNode.getAttribute("android:path")
                    scheme = if (scheme.isEmpty()) dataValue else "$scheme, $dataValue"
                }
                result[activityName] = scheme
            }
        }
    }
    return result
}