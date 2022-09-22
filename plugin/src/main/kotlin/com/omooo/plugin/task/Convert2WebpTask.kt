package com.omooo.plugin.task

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.omooo.plugin.bean.WebpToolBean
import com.omooo.plugin.ext.Convert2WebpExtension
import com.omooo.plugin.util.ImageUtil
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal open class Convert2WebpTask : DefaultTask() {

    private lateinit var config: Convert2WebpExtension

    var bigImageList = ArrayList<String>()

    private var oldSize = 0L
    private var newSize = 0L

    @TaskAction
    fun doAction() {
        config = project.extensions
            .findByType(Convert2WebpExtension::class.java) ?: return

        val hasAppPlugin = project.plugins.hasPlugin("com.android.application")
        val variants = if (hasAppPlugin) {
            project.extensions.getByType(AppExtension::class.java).applicationVariants
        } else {
            project.extensions.getByType(LibraryExtension::class.java).libraryVariants
        }

        variants.all { variant ->

            variant as BaseVariantImpl

            checkCwebpTools()

            if (!config.enableWhenDebug) {
                return@all
            }

            println("----- Convert2WebpTask run...  -----")

            val dir = variant.allRawAndroidResources.files
            val cacheList = ArrayList<String>()
            val imageFileList = ArrayList<File>()

            for (channelDir in dir) {
                traverseResDir(channelDir, imageFileList, cacheList, object : IBigImage {
                    override fun onBigImage(file: File) {
                        bigImageList.add(file.absolutePath)
                    }
                })
            }
            checkBigImage()

            println("Should handle image count: ${imageFileList.size}")
            var size = 0L
            for (file in imageFileList) {
//                println("--- ${file.absolutePath}")
                size += file.length()
            }
            println("Total Image Size: ${size / 1024}kb")
            val startTime = System.currentTimeMillis()

            dispatchOptimizeTask(imageFileList)

            println("Before optimize Size: ${oldSize / 1024}kb")
            println("After optimize Size: ${newSize / 1024}kb")
            println("Optimize Size: ${(oldSize - newSize) / 1024}kb")

            println("CostTotalTime: ${System.currentTimeMillis() - startTime}ms")
            println("------------------------------------")
        }
    }

    private fun dispatchOptimizeTask(imageFileList: java.util.ArrayList<File>) {
        if (imageFileList.size == 0 || bigImageList.isNotEmpty()) {
            return
        }
        val coreNum = Runtime.getRuntime().availableProcessors()
        if (imageFileList.size < coreNum) {
            for (file in imageFileList) {
                optimizeImage(file)
            }
        } else {
            val results = ArrayList<Future<Unit>>()
            val pool = Executors.newFixedThreadPool(coreNum)
            val part = imageFileList.size / coreNum
            for (i in 0 until coreNum) {
                val from = i * part
                val to = if (i == coreNum - 1) imageFileList.size - 1 else (i + 1) * part - 1
                results.add(pool.submit(Callable<Unit> {
                    for (index in from..to) {
                        optimizeImage(imageFileList[index])
                    }
                }))
            }
            for (f in results) {
                try {
                    f.get()
                } catch (e: Exception) {
                    println("EHiPlugin Convert2WebpTask#dispatchOptimizeTask() execute wrong.")
                }
            }
        }
    }

    private fun optimizeImage(file: File) {
        val path: String = file.path
        if (File(path).exists()) {
            oldSize += File(path).length()
        }
        ImageUtil.convert2Webp(file)
        calcNewSize(path)
    }

    private fun calcNewSize(path: String) {
        if (File(path).exists()) {
            newSize += File(path).length()
        } else {
            val indexOfDot = path.lastIndexOf(".")
            val webpPath = path.substring(0, indexOfDot) + ".webp"
            if (File(webpPath).exists()) {
                newSize += File(webpPath).length()
            } else {
                println("EHiPlugin Convert2Webp Task was wrong.")
            }
        }
    }

    private fun checkBigImage() {
        if (bigImageList.size != 0) {
            val stringBuffer = StringBuffer("Big Image Detector! ")
                .append("ImageSize can't over ${config.maxSize / 1024}kb.\n")
                .append("To fix this exception, you can increase maxSize or config them in bigImageWhiteList\n")
                .append("Big Image List: \n")
            for (fileName in bigImageList) {
                stringBuffer.append(fileName)
                stringBuffer.append("\n")
            }
            throw GradleException(stringBuffer.toString())
        }
    }

    private fun traverseResDir(
        file: File,
        imageFileList: ArrayList<File>,
        cacheList: ArrayList<String>,
        iBigImage: IBigImage
    ) {

        if (cacheList.contains(file.absolutePath)) {
            return
        } else {
            cacheList.add(file.absolutePath)
        }

        if (file.isDirectory) {
            file.listFiles()?.forEach {
                if (it.isDirectory) {
                    traverseResDir(it, imageFileList, cacheList, iBigImage)
                } else {
                    filterImage(it, imageFileList, iBigImage)
                }
            }
        } else {
            filterImage(file, imageFileList, iBigImage)
        }
    }

    private fun filterImage(file: File, imageFileList: ArrayList<File>, iBigImage: IBigImage) {
        if (config.whiteList.contains(file.name) || !ImageUtil.isImage(file)) {
            return
        }
        if ((config.isCheckSize && ImageUtil.isBigSizeImage(file, config.maxSize))
            && !config.bigImageWhiteList.contains(file.name)
        ) {
            iBigImage.onBigImage(file)
        }
        imageFileList.add(file)
    }

    private fun checkCwebpTools() {
        if (config.cwebpToolsDir.isBlank()) {
            WebpToolBean.setRootDir(project.rootDir.path)
        } else {
            WebpToolBean.setRootDir(config.cwebpToolsDir)
        }
        if (!WebpToolBean.getToolsDir().exists()) {
            throw GradleException("EHiPlugin 'convert2Webp' task need cwebp tool.")
        }
    }

    interface IBigImage {
        fun onBigImage(file: File)
    }
}