package com.omooo.plugin.bean

/**
 * Author: Omooo
 * Date: 2020/2/11
 * Version: v0.1.1
 * Desc: Convert2WebpTask 配置
 */
open class Convert2WebpExtension {
    var isEnableWhenDebug = false
    var isCheckSize = true
    var whiteList = arrayOf<String>()
    var bigImageWhiteList = arrayOf<String>()
    var cwebpToolsDir = ""
    var maxSize = (100 * 1024).toFloat()
}