package com.omooo.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.omooo.plugin.bean.WebpToolBean
import com.omooo.plugin.bean.CwebpCompressExtension
import com.omooo.plugin.util.*
import com.omooo.plugin.util.getAllChildren
import com.omooo.plugin.util.isImageFile
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Author: Omooo
 * Date: 2022/11/14
 * Desc: webp 压缩、转化
 * Use: ./gradlew compress{Variant}Webp
 * Output: projectDir/compressWebp.json
 */
internal open class CwebpCompressTask : DefaultTask() {

    @get:Internal
    lateinit var config: CwebpCompressExtension

    @get:Internal
    lateinit var variant: BaseVariant

    @TaskAction
    fun doAction() {
        println(
            """
                *********************************************
                ********** -- CwebpCompressTask -- **********
                ***** -- projectDir/compressWebp.json -- ****
                *********************************************
            """.trimIndent()
        )
        if (variant !is ApplicationVariantImpl) {
            println("${variant.name} is not an application variant.")
            return
        }
        if (!config.enable) {
            println("CwebpCompressTask ${variant.name} is not enabled")
            return
        }
        if (variant.name.lowercase().contains("debug") && !config.enableWhenDebug) {
            println("CwebpCompressTask debug is not enabled")
            return
        }
        if (config.onlyPrintImages) {
            getTotalImageFiles().map {
                it.absolutePath
            }.writeToJson("allImagesList-${variant.name.capitalize()}.json")
            return
        }

        checkCwebpTools()

        val processResult = JSONObject()
        getTotalImageFiles().apply {
            // 1. 先统计待处理的图片数量以及总计大小
            processResult.put("before_process", JSONObject().apply {
                val totalSize = map { it.length() }.reduce { element, sum -> element + sum }
                put("total_image_count", size)
                put("total_size", "${totalSize / 1024}kb")
            })

            // 2. 处理图片
            val startTime = System.currentTimeMillis()
            dispatchOptimizeTask(this).groupBy {
                it.second > it.third
            }.forEach { (compressed, l) ->
                l.sortedByDescending {
                    if (compressed) it.third else it.second
                }.apply {
                    val jsonArray = JSONArray().apply {
                        forEachIndexed { index: Int, triple: Triple<String, Long, Long> ->
                            put(index, JSONObject().apply {
                                put("file_path", triple.first)
                                put("uncompress_size", "${triple.second}bytes")
                                put("compressed_size", "${triple.third}bytes")
                            })
                        }
                    }
                    // 3. 写入未被处理和已处理的图片列表
                    processResult.put(
                        if (compressed) "compressed_list" else "uncompressed_list",
                        jsonArray
                    )
                }
                // 4. 写入最终减少大小和耗时
                if (compressed) {
                    val reduceSize =
                        l.map { it.second - it.third }.reduce { element, sum -> element + sum }
                    processResult.put("after_process", JSONObject().apply {
                        put("compressed_image_count", l.size)
                        put("uncompressed_image_count", size - l.size)
                        put("total_reduce_size", "${reduceSize / 1024}kb")
                        put("spend_time", "${System.currentTimeMillis() - startTime}ms")
                    })
                    println("""
                        Total reduce size: ${reduceSize / 1024}kb,
                        Compressed image count: ${l.size},
                        Spend time: ${System.currentTimeMillis() - startTime}ms.
                    """.trimIndent())
                }
            }
        }

        processResult.writeToJson("${project.parent?.projectDir}/compressWebp-${variant.name.capitalize()}.json")
    }

    /**
     * 获取所有的待处理图片文件列表
     */
    private fun getTotalImageFiles(): List<File> {
        return (variant as ApplicationVariantImpl).variantData.allRawAndroidResources.files.flatMap {
            it.getAllChildren()
        }.filter {
            it.filterNeedExecute()
        }.sortedByDescending {
            it.length()
        }
    }

    /**
     * 过滤需要处理的文件
     */
    private fun File.filterNeedExecute(): Boolean {
        if (config.whiteList.contains(name)) {
            return false
        }
        if (config.enableFilterWebp && name.endsWith(".webp")) {
            return false
        }
        return isImageFile()
    }

    /**
     * 分发处理任务，使用线程池来压缩
     *
     * @param imageFileList 待处理的图片文件列表
     * @return 结果集 Triple<文件路径, 原文件大小, 转化后文件大小>
     */
    private fun dispatchOptimizeTask(imageFileList: List<File>): List<Triple<String, Long, Long>> {
        if (imageFileList.isEmpty()) {
            return emptyList()
        }
        val resultList = mutableListOf<Triple<String, Long, Long>>()
        val coreNum = Runtime.getRuntime().availableProcessors()
        if (imageFileList.size < coreNum) {
            imageFileList.forEach {
                resultList.add(it.compressImage())
            }
        } else {
            val results = ArrayList<Future<List<Triple<String, Long, Long>>>>()
            val pool = Executors.newFixedThreadPool(coreNum)
            val part = imageFileList.size / coreNum
            for (i in 0 until coreNum) {
                val from = i * part
                val to = if (i == coreNum - 1) imageFileList.size - 1 else (i + 1) * part - 1
                results.add(pool.submit(Callable {
                    val result = mutableListOf<Triple<String, Long, Long>>()
                    for (index in from..to) {
                        result.add(imageFileList[index].compressImage())
                    }
                    result
                }))
            }
            for (f in results) {
                try {
                    resultList.addAll(f.get())
                } catch (e: Exception) {
                    println("""
                        Lavender CwebpCompressTask#dispatchOptimizeTask() execute wrong:
                            exception: ${e.message}
                            stacktrace: ${e.printStackTrace()}
                    """.trimIndent())
                }
            }
        }
        return resultList
    }

    /**
     * 检查是否存在 cwebp 工具
     */
    private fun checkCwebpTools() {
        if (config.cwebpToolsDir.isBlank()) {
            WebpToolBean.setRootDir(project.rootDir.path)
        } else {
            WebpToolBean.setRootDir(config.cwebpToolsDir)
        }
        if (!WebpToolBean.getToolsDir().exists()) {
            throw GradleException("Lavender 'compressWebp' task need cwebp tool.")
        }
        File(WebpToolBean.getToolsDirPath()).getAllChildren().forEach {
            // 获取文件读写权限
            Runtime.getRuntime().exec("chmod 755 ${it.absolutePath}").waitFor()
        }
    }

}