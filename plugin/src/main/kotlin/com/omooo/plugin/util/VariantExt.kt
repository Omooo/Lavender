package com.omooo.plugin.util

import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.tasks.TaskProvider
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Author: Omooo
 * Date: 2023/3/8
 * Desc: [Variant] 相关扩张函数
 */

internal val Variant.project: Project
    get() {
        return this.variantImpl.variantDependencies.javaClass.kotlin.declaredMemberProperties.first {
            it.name == "project"
        }.apply {
            isAccessible = true
        }.get(this.variantImpl.variantDependencies) as Project
    }

internal fun Variant.nameCapitalize(): String {
    return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

internal val Variant.versionName: String
    get() {
        return (this as? ApplicationVariant)?.outputs?.first()?.versionName?.get() ?: "-"
    }

/**
 * 处理资源任务
 *
 * @from LinkApplicationAndroidResourcesTask
 */
internal val Variant.processResTaskProvider: TaskProvider<out Task>?
    get() = try {
        project.tasks.named(getTaskName("process", "Resources"))
    } catch (_: UnknownTaskException) {
        println(red("processResourcesTaskProvider not found."))
        null
    }

/**
 * 优化资源任务
 *
 * @from OptimizeResourcesTask
 */
internal val Variant.optimizeResourcesTaskProvider: TaskProvider<out Task>?
    get() = try {
        project.tasks.named(getTaskName("optimize", "Resources"))
    } catch (_: UnknownTaskException) {
        println(red("optimizeResourcesTaskProvider not found."))
        null
    }

internal val Variant.processManifestTaskProvider: TaskProvider<out Task>?
    get() = try {
        this.variantImpl.taskContainer.processManifestTask
    } catch (e: Exception) {
        println(red("processManifestTaskProvider not found, e: ${e.message}"))
        null
    }

internal fun Variant.getTaskName(prefix: String, suffix: String = ""): String {
    return variantImpl.computeTaskName(prefix, suffix)
}

internal inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T