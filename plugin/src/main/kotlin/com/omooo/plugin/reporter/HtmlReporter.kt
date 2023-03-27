package com.omooo.plugin.reporter

import groovy.json.JsonOutput
import java.io.File

/**
 * Author: Omooo
 * Date: 2023/3/24
 * Desc: Html 报告生成器
 */
internal class HtmlReporter {

    /**
     * 生成报告
     */
    fun generateReport(data: AppReporter, filePath: String): File {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        var html = readResourceFile("index.html")
        val javascript = readResourceFile("frontend.js")
        html = html.replaceFirst(
            "<script src=\"frontend.js\"></script>",
            "<script>$javascript</script>"
        )
        html = html.replaceFirst("{key:\"REPLACE_ME\"}", "`${JsonOutput.toJson(data)}`")
        return file.apply {
            writeText(html)
        }
    }

    private fun readResourceFile(fileName: String): String {
        val url = requireNotNull(javaClass.getResource("/$fileName"))
        return url.readText()
    }

}