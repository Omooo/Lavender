package com.omooo.plugin.util

import java.io.File

/**
 * Created by Omooo
 * Date: 2020-02-12
 * Desc: ImageUtil
 */
class ImageUtil {

    companion object {

        fun isImage(file: File): Boolean {
            return (file.name.endsWith(".jpg")
                    || file.name.endsWith(".png")
                    || file.name.endsWith(".jpeg"))
                    && !file.name.endsWith(".9.png")
        }

        fun isBigSizeImage(file: File, maxSize: Float): Boolean {
            if (isImage(file)) {
                if (file.length() >= maxSize) {
                    return true
                }
            }
            return false
        }

        fun convert2Webp(imgFile: File) {
            if (isImage(imgFile)) {
                val webpFile =
                    File("${imgFile.path.substring(0, imgFile.path.lastIndexOf("."))}.webp")
                WebpToolUtil.cmd("cwebp", "${imgFile.path} -o ${webpFile.path} -m 6 -quiet")
                if (webpFile.length() < imgFile.length()) {
                    if (imgFile.exists()) {
                        imgFile.delete()
                    }
                } else {
                    if (webpFile.exists()) {
                        webpFile.delete()
                    }
                }
            }
        }
    }
}