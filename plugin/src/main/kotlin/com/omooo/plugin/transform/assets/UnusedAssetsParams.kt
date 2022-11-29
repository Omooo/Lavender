package com.omooo.plugin.transform.assets

import com.android.build.api.instrumentation.InstrumentationParameters
import com.omooo.plugin.bean.PrintInvokeExtension
import org.gradle.api.tasks.Input

/**
 * Author: Omooo
 * Date: 2022/11/25
 * Desc: 检测未使用的 Assets 参数
 */
internal interface UnusedAssetsParams : InstrumentationParameters {

    @get:Input
    var assetsFilePath: String
}