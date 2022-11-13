package com.omooo.plugin.util

import com.android.build.gradle.internal.dependency.ArtifactCollectionWithExtraArtifact
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.internal.component.local.model.OpaqueComponentArtifactIdentifier

/**
 * Author: Omooo
 * Date: 2022/11/13
 * Desc: 产物相关扩展方法
 */

/**
 * 获取产物的名称
 *
 * ag: "app"、":library"、"androidx.core:core-ktx:1.7.0"
 */
internal fun ResolvedArtifactResult.getArtifactName(): String {
    return when (val id = id.componentIdentifier) {
        is ProjectComponentIdentifier -> id.projectPath
        is ModuleComponentIdentifier -> id.group + ":" + id.module + ":" + id.version
        is OpaqueComponentArtifactIdentifier -> id.getDisplayName()
        is ArtifactCollectionWithExtraArtifact.ExtraComponentIdentifier -> id.getDisplayName()
        else -> throw RuntimeException("Unsupported type of ComponentIdentifier")
    }
}