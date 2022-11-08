package com.omooo.plugin.bean

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 检测方法调用的扩展类，使用方配置
 */
open class PrintInvokeExtension {
    /**
     * 方法调用列表（方法的全限定名）
     *
     * ag: ["android/widget/Toast#show()V", "xxx"]
     */
    var methodList = arrayOf<String>()

    /**
     * 包名列表
     *
     * ag: ["android/", "xxx"]
     */
    var packageList = arrayOf<String>()
}