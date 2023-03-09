package com.omooo.plugin.util

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.dependency.ArtifactCollectionWithExtraArtifact
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.internal.component.local.model.OpaqueComponentArtifactIdentifier
import java.io.File

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

/**
 * 获取特定类型的产物文件列表
 *
 * @param artifactType [AndroidArtifacts.ArtifactType] 类型
 */
internal fun ApplicationVariantImpl.getArtifactFiles(artifactType: AndroidArtifacts.ArtifactType): List<File> {
    return variantData.variantDependencies.getArtifactCollection(
        AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
        AndroidArtifacts.ArtifactScope.ALL,
        artifactType
    ).artifacts.map { artifact ->
        artifact.file
    }
}

/**
 * 类全限定名到 (AAR 名, 该类文件大小) 的映射
 * 例如: "androidx.core.graphics.PaintKt" to Pair("androidx.core:core-ktx:1.7.0", 2333)
 */
internal fun ApplicationVariantImpl.getClassMap(): Map<String, Pair<String, Long>> {
    return variantData.variantDependencies.getArtifactCollection(
        AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
        AndroidArtifacts.ArtifactScope.ALL,
        AndroidArtifacts.ArtifactType.CLASSES
    ).artifacts.associate { artifact ->
        artifact.getArtifactName() to artifact.file.parseJar()
    }.flatMap { (key, valueList) ->
        valueList.map { valuePair ->
            // kotlin/io/path/ExperimentalPathApi.class to kotlin.io.path.ExperimentalPathApi
            val className = valuePair.first.substringBeforeLast(".").replace("/", ".")
            Pair(className, Pair(key, valuePair.second))
        }
    }.groupBy({ it.first }, { it.second }).mapValues { (_, valueList) -> valueList.first() }
}