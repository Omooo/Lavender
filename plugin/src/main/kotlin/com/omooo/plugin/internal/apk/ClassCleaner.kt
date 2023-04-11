package com.omooo.plugin.internal.apk

import com.android.tools.proguard.ProguardMap
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.reporter.common.FileType
import com.omooo.plugin.util.formatDollar
import java.io.File

/**
 * Author: Omooo
 * Date: 2023/3/17
 * Desc: 类文件解混淆
 */
internal class ClassCleaner(mappingFile: File) : ICleaner {

    private val proguardMap = ProguardMap().apply {
        readFromFile(mappingFile)
    }

    override fun isApplicable(appFile: AppFile): Boolean {
        return appFile.fileType == FileType.CLASS
    }

    override fun clean(appFile: AppFile): AppFile {
        val className = appFile.name
            .removeSurrounding("L", ";")
            .removeSuffix(".class")
            .replace("/", ".")
        return appFile.apply {
            name = proguardMap.getClassName(className).formatDollar()
            fileType = FileType.CLASS
        }
    }


}