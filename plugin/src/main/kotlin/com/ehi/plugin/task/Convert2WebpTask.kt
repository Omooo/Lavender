package com.ehi.plugin.task

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.ehi.plugin.ext.Convert2WebpExtension
import com.ehi.plugin.util.ImageUtil
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File

internal open class Convert2WebpTask : DefaultTask() {

    private lateinit var config: Convert2WebpExtension

    var bigImageList = ArrayList<String>()

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

            println("--- Convert2WebpTask run... ---")

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

            println("Convert2WebpTask 需要处理的图片数量：${imageFileList.size}")
            println("图片列表：")
            for (file in imageFileList) {
                println("--- ${file.absolutePath}")
            }

            val startTime = System.currentTimeMillis()
            dispatchOptimizeTask(imageFileList)
            println("--- Convert2WebpTask execute end. ---")
            println("--- CostTotalTime: ${System.currentTimeMillis() - startTime}ms ---")

        }
    }

    private fun dispatchOptimizeTask(imageFileList: java.util.ArrayList<File>) {

    }

    private fun checkBigImage() {
        if (bigImageList.size != 0) {
            val stringBuffer = StringBuffer("Big Image Detector!")
                .append("To fix this exception, you can config them into bigImageWhiteList which in build.gradle file")
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

    }

    interface IBigImage {
        fun onBigImage(file: File)
    }
}