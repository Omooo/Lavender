package com.omooo.plugin.bean

import com.omooo.plugin.task.CheckSchemeModifiedTask
import java.io.File

/**
 * Author: Omooo
 * Date: 2023/08/22
 * Desc: [CheckSchemeModifiedTask] 配置
 */
open class CheckSchemeModifiedExtension {

    /** 开启该任务，默认不开启 */
    var enable = false

    /** 基线对比文件 */
    var baselineSchemeFile: File? = null
}