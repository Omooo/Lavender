package com.omooo.plugin.transform

import com.android.build.api.instrumentation.InstrumentationParameters
import com.omooo.plugin.bean.PrintInvokeExtension
import org.gradle.api.tasks.Input

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 检测方法调用的参数
 */
internal interface CheckInvokeParams : InstrumentationParameters {

    @get:Input
    var methodList: Array<String>

    @get:Input
    var packageList: Array<String>
}