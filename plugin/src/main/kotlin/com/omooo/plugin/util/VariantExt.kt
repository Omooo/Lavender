package com.omooo.plugin.util

import com.android.build.api.component.impl.ComponentImpl
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.BaseVariantImpl
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.tasks.TaskProvider
import java.util.*

/**
 * Author: Omooo
 * Date: 2023/3/8
 * Desc: [BaseVariant] 相关扩张函数
 */

internal fun BaseVariant.nameCapitalize(): String {
    return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

/**
 * 返回 Android 平台内的自定义 View
 *
 * @return
 */
internal fun BaseVariant.getAndroidPlatformWidgets(): Set<String> {
    return extension.bootClasspath.find {
        it.name == "android.jar"
    }?.parentFile?.file("data", "widgets.txt")?.readLines()?.filter {
        it.startsWith("W")
    }?.map {
        it.substring(1, it.indexOf(' '))
    }?.toSet() ?: emptySet()
}

internal val BaseVariant.processManifestTaskProvider: TaskProvider<out Task>?
    get() = try {
        project.tasks.named(getTaskName("process", "Manifest"))
    } catch (_: UnknownTaskException) {
        println(red("processManifestTaskProvider not found."))
        null
    }

internal val BaseVariant.bundleClassesTaskProvider: TaskProvider<out Task>?
    get() = try {
        // for AGP < 7.2.0
        project.tasks.named(getTaskName("bundle", "Classes"))
    } catch (_: UnknownTaskException) {
        // for AGP >= 7.2.0+
        try {
            project.tasks.named(getTaskName("bundle", "ClassesToRuntimeJar"))
        } catch (_: UnknownTaskException) {
            try {
                project.tasks.named(getTaskName("bundle", "ClassesToCompileJar"))
            } catch (_: UnknownTaskException) {
                null
            }
        }
    }

internal val BaseVariant.createFullJarTaskProvider: TaskProvider<out Task>?
    get() = try {
        project.tasks.named(getTaskName("createFullJar"))
    } catch (_: UnknownTaskException) {
        null
    }

internal fun BaseVariant.getTaskName(prefix: String, suffix: String = ""): String {
    return component.computeTaskName(prefix, suffix)
}

internal val BaseVariant.extension: BaseExtension
    get() = project.getAndroid()

internal val BaseVariant.component: ComponentImpl
    get() = BaseVariantImpl::class.java.getDeclaredField("component").apply {
        isAccessible = true
    }.get(this) as ComponentImpl

internal val BaseVariant.project: Project
    get() = component.variantDependencies.run {
        javaClass.getDeclaredField("project").apply {
            isAccessible = true
        }.get(this) as Project
    }

internal inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T