package com.omooo.plugin.bean

/**
 * Author: Omooo
 * Date: 2020/2/11
 * Version: v0.1.1
 * Desc: Convert2WebpTask 配置
 */
open class CwebpCompressExtension {

    /** 开启该任务，默认不开启 */
    var isEnable = false

    /** debug 是否开启该任务，默认不开启 */
    var isEnableWhenDebug = false

    /** 白名单 */
    var whiteList = arrayOf("ic_launcher.png", "ic_launcher_round.png")

    /** cwebp 工具的目录 */
    var cwebpToolsDir = ""
}