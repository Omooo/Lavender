package com.omooo.plugin.util

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.dependency.ArtifactCollectionWithExtraArtifact
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.internal.component.local.model.OpaqueComponentArtifactIdentifier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.Duration
import java.util.zip.ZipInputStream

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
internal fun ApplicationVariantImpl.getArtifactClassMap(): Map<String, Pair<String, Long>> {
    return variantData.variantDependencies.getArtifactCollection(
        AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
        AndroidArtifacts.ArtifactScope.ALL,
        AndroidArtifacts.ArtifactType.CLASSES
    ).artifacts.associate { artifact ->
        // org.jetbrains.kotlin:kotlin-stdlib-common:1.7.21 to xxx/.gradle/caches/xxx/xxx.jar
        artifact.getArtifactName() to artifact.file.parseJar()
    }.flatMap { (key, valueList) ->
        valueList.map { valuePair ->
            // kotlin/io/path/ExperimentalPathApi.class to kotlin.io.path.ExperimentalPathApi
            val className = valuePair.first.substringBeforeLast(".").replace("/", ".")
            Pair(className, Pair(key, valuePair.second))
        }
    }.groupBy({ it.first }, { it.second }).mapValues { (_, valueList) -> valueList.first() }
}

internal fun ApplicationVariantImpl.getAllClasses(): Map<String, ClassNode> {
    return variantData.variantDependencies.getArtifactCollection(
        AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
        AndroidArtifacts.ArtifactScope.ALL,
        AndroidArtifacts.ArtifactType.CLASSES_JAR
    ).artifacts.map { artifact ->
        artifact.getArtifactName() to artifact.file
    }.map {
        val startTime = System.currentTimeMillis()
        val classes = loadClasses(ZipInputStream(FileInputStream(it.second)))
        println("Load ${green(classes.size)} classes from ${it.first}: ${yellow(System.currentTimeMillis() - startTime)} ms")
        classes
    }.flatten().associateBy { it.name.replace("/", ".") }
}

private fun loadClasses(zip: ZipInputStream): List<ClassNode> {
    fun parse(input: InputStream): ClassNode = ClassNode().also { klass ->
        ClassReader(input.readBytes()).accept(klass, 0)
    }

    val classes = mutableListOf<ClassNode>()
    while (true) {
        val entry = zip.nextEntry ?: break
        classes += when {
            entry.name.endsWith(".class", true) -> listOf(parse(zip))
            entry.name == "classes.jar" -> loadClasses(ZipInputStream(zip))
            else -> emptyList()
        }
    }
    return classes
}