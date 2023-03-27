package com.omooo.plugin.internal.apk

import com.android.SdkConstants
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.SdkLocator
import com.android.builder.errors.DefaultIssueReporter
import com.android.prefs.AndroidLocationsSingleton
import com.android.repository.api.ProgressIndicatorAdapter
import com.android.sdklib.repository.AndroidSdkHandler
import com.android.utils.StdLogger
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.FileType
import com.omooo.plugin.util.encode
import com.omooo.plugin.util.project
import com.omooo.plugin.util.red
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.absolutePathString
import kotlin.io.path.extension

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: 资源解混淆
 */
internal class ResourceCleaner(
    private val buildDir: File,
    private val variant: BaseVariant,
) : ICleaner {

    // 资源名映射，ag: "res/eU.xml" -> "res/anim/abc_popup_exit.xml"
    private val resourceNameMap: Map<String, String>
    private var resMapping: Map<String, String> = emptyMap()

    init {
        // 由于并没有资源的 mapping 文件，不过 OptimizeResourcesTask 默认只做 '--shorten-resource-paths' 优化
        // 所以可以对比俩文件的 md5 值来生成混淆后的文件名和源文件名的映射
        val originMap = getApFile("processed_res").createFileMd5Map()
        val optimizedMap = getApFile("optimized_processed_res").createFileMd5Map()
        resourceNameMap = originMap.filterKeys {
            optimizedMap.containsKey(it)
        }.mapKeys {
            optimizedMap.getOrDefault(it.key, "unknown-resource")
        }
        val resMappingFile = buildDir.resolve("mapping-res.txt")
        if (resMappingFile.exists()) {
            resMappingFile.delete()
        }
        val rc = variant.project.exec { spec ->
            spec.isIgnoreExitValue = true
            spec.commandLine = listOf(
                getAapt2Location().absolutePathString(),
                "optimize",
                getApFile("processed_res").absolutePath,
                "--shorten-resource-paths",
                "--resource-path-shortening-map",
                resMappingFile.absolutePath
            )
        }
        when (rc.exitValue) {
            0 -> {
                resMapping = resMappingFile.readLines().associate {
                    it.substringAfter("-> ") to it.substringBefore(" ->")
                }
            }
            else -> println(red("Aapt2 execute failed: ${rc.exitValue}"))
        }
    }

    override fun isApplicable(appFile: AppFile): Boolean {
        return appFile.name.startsWith("res/")
    }

    override fun clean(appFile: AppFile): AppFile {
        return appFile.apply {
            // res/color 是不会被混淆的，所以找不到的话直接使用原名
            // https://android.googlesource.com/platform/frameworks/base/+/master/tools/aapt2/optimize/ResourcePathShortener.cpp
            name = resMapping.getOrDefault(appFile.name, appFile.name)
            fileType = FileType.RESOURCE
        }
    }

    /**
     * 获取资源的 AP 文件
     */
    private fun getApFile(resDir: String): File {
        return Files.walk(buildDir.resolve("intermediates/$resDir/${variant.name}").toPath())
            .filter {
                it.extension == SdkConstants.EXT_RES
            }.findFirst().get().toFile()
    }

    /**
     * 构建 File.md5 -> File.name 的映射
     */
    private fun File.createFileMd5Map(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        ZipFile(this).use { zipFile ->
            zipFile.entries().toList().filterNot { it.isDirectory }.forEach { entry ->
                val md5 = zipFile.getInputStream(entry).readBytes().encode()
                result[md5] = entry.name
            }
        }
        return result
    }

    /**
     * 获取 AAPT2 目录
     */
    private fun getAapt2Location(): Path {
        val sdkLocation = getAndroidSdkLocation()
        val sdkHandler = AndroidSdkHandler.getInstance(AndroidLocationsSingleton, sdkLocation)
        val progressIndicator = object : ProgressIndicatorAdapter() {}
        val buildToolInfo = sdkHandler.getLatestBuildTool(progressIndicator, true)
        return buildToolInfo.location.resolve(SdkConstants.FN_AAPT2)
    }

    /**
     * 获取 SDK 目录
     */
    private fun getAndroidSdkLocation(): Path {
        val logger = StdLogger(StdLogger.Level.WARNING)
        val issueReporter = DefaultIssueReporter(logger)
        return SdkLocator.getSdkDirectory(variant.project.rootDir, issueReporter).toPath()
    }
}