package com.omooo.plugin.internal.apk

import com.android.build.gradle.api.BaseVariant
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.project

/**
 * Author: Omooo
 * Date: 2023/3/19
 * Desc: [AppFile] 清洗
 */

internal fun List<AppFile>.clear(variant: BaseVariant): List<AppFile> {

    val clearList: List<ICleaner> = listOf(
        ClassCleaner(variant.mappingFileProvider.get().singleFile),
        ResourceCleaner(variant.project.buildDir, variant),
        TypeAssigningCleaner()
    )
    return clearList.flatMap { cleaner ->
        this.filter {
            cleaner.isApplicable(it)
        }.map {
            cleaner.clean(it)
        }
    }
}