package com.omooo.plugin.internal.apk

import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.FileType

/**
 * Author: Omooo
 * Date: 2023/3/19
 * Desc: 类型赋值
 */
internal class TypeAssigningCleaner : ICleaner {

    override fun isApplicable(appFile: AppFile): Boolean {
        return appFile.fileType == FileType.OTHER
    }

    override fun clean(appFile: AppFile): AppFile {
        return appFile.apply {
            if (name.startsWith("lib/")) {
                fileType = FileType.NATIVE_LIB
            }
            if (name.startsWith("assets/")) {
                fileType = FileType.ASSET
            }
        }
    }

}