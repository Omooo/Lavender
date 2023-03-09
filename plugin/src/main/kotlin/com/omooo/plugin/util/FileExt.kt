package com.omooo.plugin.util

import java.io.File
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.io.path.isRegularFile
import kotlin.streams.toList

/**
 * Author: Omooo
 * Date: 2022/11/14
 * Desc: File 相关扩展方法
 */

/**
 * 获取当前文件夹下所有子文件（不包含文件夹）
 */
internal fun File.getAllChildren(): List<File> {
    return takeIf {
        exists()
    }?.run {
        Files.walk(this.toPath()).filter {
            it.isRegularFile()
        }.map {
            it.toFile()
        }.toList()
    } ?: emptyList()
}

/**
 * 是否是图片文件（.9 图不属于图片，不可压缩）
 *
 * @return true: 是
 */
internal fun File.isImageFile(): Boolean {
    if (name.endsWith(".9.png")) {
        return false
    }
    return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")
            || name.endsWith(".webp")
}

/**
 * 图片压缩
 *
 * @return Triple<文件路径, 原文件大小, 压缩后文件大小>
 */
internal fun File.compressImage(): Triple<String, Long, Long> {
    val sourceImageSize = length()
    if (name.endsWith(".webp")) {
        WebpToolUtil.cmd("cwebp", "$path -o $path -m 6 -quiet")
        return Triple(absolutePath, sourceImageSize, length())
    }
    val webpFile = File("${path.substring(0, path.lastIndexOf("."))}.webp")
    WebpToolUtil.cmd("cwebp", "$path -o ${webpFile.path} -m 6 -quiet")
    if (webpFile.length() < length()) {
        if (exists() && delete()) {
            return Triple(webpFile.absolutePath, sourceImageSize, webpFile.length())
        }
    }
    // 转化后的 webp 文件还比原文件大，则保留原文件
    val webpFileSize = webpFile.length()
    if (webpFile.exists() && this.exists()) {
        webpFile.delete()
    }
    return Triple(absolutePath, sourceImageSize, webpFileSize)
}

/**
 * 从文件绝对路径中获取 AAR 名称
 *
 * @return ag: appcompat-1.3.0、core-1.7.0
 */
internal fun String.getAarNameFromPath(default: String = "unknown"): String {
    val prefix = "transformed/"
    if (!contains(prefix)) {
        return default
    }
    return substring(indexOf(prefix) + prefix.length, lastIndexOf("/res/"))
}


/**
 * 解析 Jar 生成文件列表（文件名 -> 文件大小，单位字节）
 *
 * @return ag: { "kotlin.io.path.ExperimentalPathApi" to 233, }
 */
internal fun File.parseJar(): List<Pair<String, Long>> {
    return JarFile(this).entries().toList().filterNot(JarEntry::isDirectory).map {
        it.name to it.compressedSize
    }
}