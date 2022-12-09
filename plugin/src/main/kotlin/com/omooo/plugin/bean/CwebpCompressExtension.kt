package com.omooo.plugin.bean

/**
 * Author: Omooo
 * Date: 2020/2/11
 * Version: v0.1.1
 * Desc: Convert2WebpTask 配置
 */
open class CwebpCompressExtension {

    /** 开启该任务，默认不开启 */
    var enable = false

    /** debug 是否开启该任务，默认不开启 */
    var enableWhenDebug = false

    /** 白名单 */
    var whiteList = arrayOf("ic_launcher.png", "ic_launcher_round.png")

    /** 过滤掉 webp 图片 */
    var enableFilterWebp = true

    /** 只输出图片 */
    var onlyPrintImages = false

    /** cwebp 工具的目录 */
    var cwebpToolsDir = ""
}