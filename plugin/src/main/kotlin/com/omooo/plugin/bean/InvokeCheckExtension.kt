package com.omooo.plugin.bean

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 检测方法调用的扩展类，使用方配置
 */
open class InvokeCheckExtension {
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

    /**
     * 常量列表
     *
     * ag: ["android.permission.READ_EXTERNAL_STORAGE", "xxx"]
     */
    var constantsList = arrayOf<String>()



    /* -------------------------- internal ----------------------- */

    /**
     * 是否开启，true: 开启
     * 校验条件: 检测列表有一项不为空
     */
    internal fun enable(): Boolean {
        return methodList.isNotEmpty() || packageList.isNotEmpty() || constantsList.isNotEmpty()
    }

    /**
     * 获取方法列表
     *
     * @return Tripe<className, methodName, methodDesc>
     */
    internal fun getMethodList(): List<Triple<String, String, String>> {
        return methodList.filter {
            it.isNotEmpty()
        }.map {
            val owner = it.substringBefore("#").replace(".", "/")
            val name = it.substringAfter("#", "").substringBefore("(")
            val desc = if (name.isNotEmpty()) it.substringAfterLast(name, "") else ""
            Triple(owner, name, desc)
        }
    }

    /**
     * 获取包名列表
     */
    internal fun getPackageList(): List<String> {
        return packageList.filter {
            it.isNotEmpty()
        }.map {
            it.replace(".", "/")
        }
    }
}