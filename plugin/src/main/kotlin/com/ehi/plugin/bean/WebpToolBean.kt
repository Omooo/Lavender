package com.ehi.plugin.bean

import java.io.File

/**
 * Created by Omooo
 * Date: 2020-02-13
 * Desc: cwebp 工具路径
 */
object WebpToolBean {
    private lateinit var rootDir: String

    fun setRootDir(rootDir: String) {
        this.rootDir = rootDir
    }

    fun getRootDirPath(): String {
        return rootDir
    }

    fun getToolsDir(): File {
        return File("$rootDir/tools/cwebp")
    }

    fun getToolsDirPath(): String {
        return "$rootDir/tools/cwebp"
    }
}