package com.omooo.plugin.util

import groovy.json.JsonOutput
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.charset.Charset

/**
 * Author: Omooo
 * Date: 2023/1/8
 * Desc: Transform 阶段输出报告
 */
internal object TransformReporter {

    private const val DIR_REPORTER = "lavender-plugin/reporter"

    /**
     * 删除 Transform 报告文件夹
     */
    fun deleteTransformReporterDir() {
        File(DIR_REPORTER).takeIf {
            it.exists()
        }?.deleteRecursively()
    }

    /**
     * 逐行写入 Json
     *
     * @param fileName 文件名
     * @param key Json 的 key；
     * @param value Json 的 value；类型为 JSONArray
     */
    fun writeJsonLineByLine(fileName: String, key: String, value: String) {
        runCatching {
            val file = File(File(DIR_REPORTER).apply {
                takeIf { !it.exists() }?.mkdirs()
            }, fileName).apply {
                takeIf { !exists() }?.createNewFile()
                if (readText().isEmpty()) {
                    writeText("{}")
                }
            }
            JSONObject(file.readText()).let {
                if (it.optJSONArray(key) == null) {
                    it.put(key, JSONArray().apply {
                        put(value)
                    })
                } else if (!it.getJSONArray(key).contains(value)) {
                    it.getJSONArray(key).put(value)
                }
                Pair(file, it.toString())
            }
        }.onSuccess { (file, text) ->
            PrintWriter(FileWriter(file, Charset.defaultCharset()))
                .use { it.write(JsonOutput.prettyPrint(text)) }
        }.onFailure {
            println("TransformReporter#writeJsonLineByLine throw Exception: ${it.message}")
        }
    }
}