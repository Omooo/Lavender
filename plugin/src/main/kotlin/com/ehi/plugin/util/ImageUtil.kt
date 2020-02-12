package com.ehi.plugin.util

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
    }
}