package com.omooo.plugin.transform.invoke

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.tasks.Input

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 检测方法、常量等调用的参数
 */
internal interface InvokeCheckParams : InstrumentationParameters {

    @get:Input
    var methodList: List<Triple<String, String, String>>

    @get:Input
    var packageList: List<String>

    @get:Input
    var constantsList: List<String>
}