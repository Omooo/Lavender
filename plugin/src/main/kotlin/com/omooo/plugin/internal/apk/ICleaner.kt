package com.omooo.plugin.internal.apk

import com.omooo.plugin.reporter.common.AppFile

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: 解混淆、类型赋值等
 */
internal interface ICleaner {

    fun isApplicable(appFile: AppFile): Boolean

    fun clean(appFile: AppFile): AppFile

}