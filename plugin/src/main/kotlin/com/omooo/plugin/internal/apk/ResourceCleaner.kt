package com.omooo.plugin.internal.apk

import com.android.SdkConstants
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.FileType
import com.omooo.plugin.util.encode
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipFile
import kotlin.io.path.extension

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: 资源解混淆
 */
internal class ResourceCleaner(
    private val buildDir: File,
    private val variantName: String
) : ICleaner {

    // 资源名映射，ag: "res/eU.xml" -> "res/anim/abc_popup_exit.xml"
    private val resourceNameMap: Map<String, String>

    init {
        // 由于并没有资源的 mapping 文件，不过 OptimizeResourcesTask 默认只做 '--shorten-resource-paths' 优化
        // 所以可以对比俩文件的 md5 值来生成混淆后的文件名和源文件名的映射
        val originMap = getApFile("processed_res").createFileMd5Map()
        val optimizedMap = getApFile("optimized_processed_res").createFileMd5Map()
        resourceNameMap = originMap.mapKeys {
            optimizedMap.getOrDefault(it.key, "unknown")
        }
    }

    override fun isApplicable(appFile: AppFile): Boolean {
        return appFile.name.startsWith("res/")
    }

    override fun clean(appFile: AppFile): AppFile {
        return appFile.apply {
            name = resourceNameMap.getOrDefault(appFile.name, "unknown")
            fileType = FileType.RESOURCE
        }
    }

    /**
     * 获取资源的 AP 文件
     */
    private fun getApFile(resDir: String): File {
        return Files.walk(buildDir.resolve("intermediates/$resDir/$variantName").toPath()).filter {
            it.extension == SdkConstants.EXT_RES
        }.findFirst().get().toFile()
    }

    /**
     * 构建 File.md5 -> File.name 的映射
     */
    private fun File.createFileMd5Map(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        ZipFile(this).use { zipFile ->
            zipFile.entries().iterator().asSequence().forEach { entry ->
                val md5 = zipFile.getInputStream(entry).readBytes().encode()
                result[md5] = entry.name
            }
        }
        return result
    }
}