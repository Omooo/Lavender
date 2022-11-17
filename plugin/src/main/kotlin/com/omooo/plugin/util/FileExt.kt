package com.omooo.plugin.util

import java.io.File

/**
 * Author: Omooo
 * Date: 2022/11/14
 * Desc: File 相关扩展方法
 */

/**
 * 获取当前文件夹下所有文件
 */
internal fun File.getAllChildren(): List<File> {
    if (!isDirectory) {
        return listOf(this)
    }
    val list = arrayListOf<File>()
    listFiles()?.forEach { file ->
        if (file.isDirectory) {
            list.addAll(file.getAllChildren())
        } else {
            list.add(file)
        }
    }
    return list
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
 * 图片转化为 webp 格式
 *
 * @return Triple<文件路径, 原文件大小, 转化后文件大小>
 */
internal fun File.imageConvert2Webp(): Triple<String, Long, Long> {
    val sourceImageSize = length()
    val webpFile = File("${path.substring(0, path.lastIndexOf("."))}.webp")
    WebpToolUtil.cmd("cwebp", "$path -o ${webpFile.path} -m 6 -quiet")
    if (webpFile.length() < length()) {
        if (exists()) {
            delete()
        }
        return Triple(webpFile.absolutePath, sourceImageSize, webpFile.length())
    }
    // 转化后的 webp 文件还比原文件大，则保留原文件
    val webpFileSize = webpFile.length()
    if (webpFile.exists()) {
        webpFile.delete()
    }
    return Triple(absolutePath, sourceImageSize, webpFileSize)
}