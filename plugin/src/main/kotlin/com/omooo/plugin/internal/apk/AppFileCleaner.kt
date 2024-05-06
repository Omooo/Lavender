package com.omooo.plugin.internal.apk

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import com.omooo.plugin.reporter.common.AppFile
import com.omooo.plugin.util.project
import com.omooo.plugin.util.variantImpl

/**
 * Author: Omooo
 * Date: 2023/3/19
 * Desc: [AppFile] 清洗
 */

internal fun List<AppFile>.clear(variant: Variant): List<AppFile> {

    val mappingFile = variant.variantImpl.artifacts.get(SingleArtifact.OBFUSCATION_MAPPING_FILE)
    val clearList: List<ICleaner> = listOf(
        ClassCleaner(mappingFile.get().asFile),
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