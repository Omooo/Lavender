package com.omooo.plugin.util

import groovy.json.JsonOutput
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * Author: Omooo
 * Date: 2019/10/8
 * Version: v0.1.1
 * Desc: 一系列 Kotlin 扩展函数
 */

/**
 * 生成 ByteArray 的 MD5 值
 */
fun ByteArray.encode(): String {
    val instance: MessageDigest = MessageDigest.getInstance("MD5")
    val digest: ByteArray = instance.digest(this)
    val sb = StringBuffer()
    for (b in digest) {
        val i: Int = b.toInt() and 0xff
        var hexString = Integer.toHexString(i)
        if (hexString.length < 2) hexString = "0$hexString"
        sb.append(hexString)
    }
    return sb.toString()
}

/**
 * 将 Map 以 Json 文件输出
 */
fun <K, V> Map<K, V>.writeToJson(path: String) {
    val jsonFile = File(path)
    if (jsonFile.exists()) {
        jsonFile.delete()
    }
    jsonFile.createNewFile()
    val json = JsonOutput.toJson(this)
    jsonFile.writeText(JsonOutput.prettyPrint(json), Charsets.UTF_8)
    println("Reporter: ${jsonFile.toPath().toUri()}")
}

/**
 * 将 List 以 Json 文件输出
 */
fun <T> List<T>.writeToJson(path: String) {
    val jsonFile = File(path)
    if (jsonFile.exists()) {
        jsonFile.delete()
    }
    jsonFile.createNewFile()
    val json = JsonOutput.toJson(this)
    jsonFile.writeText(JsonOutput.prettyPrint(json), Charsets.UTF_8)
    println("Reporter: ${jsonFile.toPath().toUri()}")
}

/**
 * 将 List 以 Json 文件输出
 */
fun JSONObject.writeToJson(path: String) {
    val jsonFile = File(path)
    if (jsonFile.exists()) {
        jsonFile.delete()
    }
    jsonFile.createNewFile()
    jsonFile.writeText(JsonOutput.prettyPrint(this.toString()), Charsets.UTF_8)
    println("Reporter: ${jsonFile.toPath().toUri()}")
}

/**
 * 将 Json string text 写入 json 文件
 */
internal fun String.writeToJson(filePath: String) {
    val jsonFile = File(filePath)
    if (jsonFile.exists()) {
        jsonFile.delete()
    }
    jsonFile.createNewFile()
    jsonFile.writeText(JsonOutput.prettyPrint(this), Charsets.UTF_8)
    println("Reporter: ${jsonFile.toPath().toUri()}")
}

/**
 * 将对象以 Json 文件输出
 */
internal fun Any.writeToJson(path: String) {
    File(path).apply {
        if (exists()) {
            delete()
        }
        createNewFile()
        val json = JsonOutput.toJson(this@writeToJson)
        writeText(JsonOutput.prettyPrint(json), Charsets.UTF_8)
        println("Reporter: ${toPath().toUri()}")
    }
}
