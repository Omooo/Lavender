package com.omooo.plugin.transform

import com.android.build.api.instrumentation.InstrumentationParameters
import com.omooo.plugin.bean.PrintInvokeExtension
import org.gradle.api.tasks.Input

internal interface CheckParams : InstrumentationParameters {

    @get:Input
    var methodList: Array<String>

    @get:Input
    var packageList: Array<String>
}