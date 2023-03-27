package com.omooo.plugin.internal.apk

import com.android.SdkConstants
import com.android.tools.apk.analyzer.dex.DexFiles
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.FileType
import java.io.File
import java.util.zip.ZipFile

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: APK 解析
 */
internal class ApkParser {

    /**
     * 解析 Apk
     */
    fun parse(apkFile: File): List<AppFile> {
        val result = mutableListOf<AppFile>()
        ZipFile(apkFile).use { zipFile ->
            zipFile.entries().iterator().forEach { entry ->
                if (entry.name.endsWith(SdkConstants.DOT_DEX, true)) {
                    result.addAll(parseDex(zipFile.getInputStream(entry).readBytes()))
                } else {
                    result.add(AppFile(entry.name, entry.compressedSize))
                }
            }
        }
        return result
    }

    /**
     * 解析 Dex 文件
     */
    private fun parseDex(byteArray: ByteArray): List<AppFile> {
        return DexFiles.getDexFile(byteArray).classes.map {
            AppFile(it.type, it.size.toLong(), fileType = FileType.CLASS)
        }
    }
}