package com.omooo.plugin.util

import com.android.build.api.component.analytics.AnalyticsEnabledVariant
import com.android.build.api.variant.Variant
import com.android.build.api.variant.impl.VariantImpl
import com.android.build.gradle.internal.dependency.ArtifactCollectionWithExtraArtifact
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.omooo.plugin.internal.ArtifactType
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.attributes.Attribute
import org.gradle.internal.component.local.model.OpaqueComponentArtifactIdentifier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
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
internal fun Variant.getArtifactFiles(artifactType: AndroidArtifacts.ArtifactType): List<File> {
    return this.variantImpl.variantDependencies.getArtifactCollection(
        AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
        AndroidArtifacts.ArtifactScope.ALL,
        artifactType
    ).artifacts.map { artifact ->
        artifact.file
    }
}

internal fun Variant.getArtifactCollection(artifactType: AndroidArtifacts.ArtifactType): ArtifactCollection {
    return this.variantImpl.variantDependencies.getArtifactCollection(
        AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
        AndroidArtifacts.ArtifactScope.ALL,
        artifactType
    )
}

/**
 * 获取所有的依赖
 */
internal fun Variant.getDependencies(): Map<String, List<Pair<String, Long>>> {
    val result = mutableMapOf<String, MutableList<Pair<String, Long>>>()
    val config = project.configurations.getByName("${name}RuntimeClasspath")
    ArtifactType.entries.forEach { artifactType ->
        getArtifactView(config, artifactType.type).forEach {
            val components = result.getOrPut(it.getArtifactName()) { ArrayList() }
            components += it.file.parseArtifact(it.file.absolutePath, artifactType.prefix)
        }
    }
    return result
}

internal fun Variant.getRuntimeClasspath(): List<ResolvedArtifactResult> {
    val config = project.configurations.getByName("${name}RuntimeClasspath")
    val map = mutableMapOf<String, MutableList<Pair<String, Long>>>()
    this.getRuntimeClasspath().forEach {
        val components = map.getOrPut(it.getArtifactName()) { ArrayList() }
        components += it.file.parseArtifact(it.file.absolutePath)
    }
    return listOf("android-classes", "android-res", "android-assets", "android-jni").flatMap {
        getArtifactView(config, it)
    }
}

private fun getArtifactView(configuration: Configuration, artifactType: String) =
    configuration.incoming.artifactView { viewConfiguration ->
        viewConfiguration.attributes { attributeContainer ->
            attributeContainer.attribute(
                Attribute.of("artifactType", String::class.java),
                artifactType
            )
        }
    }.artifacts.artifacts

internal val Variant.variantImpl: VariantImpl<*>
    get() = when (this) {
        is VariantImpl<*> -> this
        is AnalyticsEnabledVariant -> this.delegate as VariantImpl<*>
        else -> TODO("No implementationed!")
    }

/**
 * 类全限定名到 (AAR 名, 该类文件大小) 的映射
 * 例如: "androidx.core.graphics.PaintKt" to Pair("androidx.core:core-ktx:1.7.0", 2333)
 */
internal fun Variant.getArtifactClassMap(): Map<String, Pair<String, Long>> {
    return variantImpl.variantDependencies.getArtifactCollection(
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

internal fun Variant.getAllClasses(): Map<String, ClassNode> {
    return variantImpl.variantDependencies.getArtifactCollection(
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