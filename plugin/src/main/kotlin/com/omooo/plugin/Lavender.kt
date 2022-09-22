package com.omooo.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.omooo.plugin.ext.Convert2WebpExtension
import com.omooo.plugin.spi.VariantProcessor
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: Plugin
 */
class Lavender : Plugin<Project> {

    override fun apply(project: Project) {

        println("apply plugin: 'Lavender'")

//        project.repositories.maven {
//            it.url = URI("")
//        }
//        project.dependencies.add("api", "com.omooo.plugin:annotation:0.1.1")

        // Extension
        project.extensions.create("convert2WebpConfig", Convert2WebpExtension::class.java)

        when {
            project.plugins.hasPlugin("com.android.application") -> project.extensions.getByType(
                AppExtension::class.java
            ).let { android ->
                project.afterEvaluate {
                    ServiceLoader.load(VariantProcessor::class.java, javaClass.classLoader)
                        .toList().let { processes ->
                            android.applicationVariants.forEach { variant ->
                                processes.forEach {
                                    it.process(variant)
                                }
                            }
                        }
                }
            }

            project.plugins.hasPlugin("com.android.library") -> project.extensions.getByType(
                LibraryExtension::class.java
            ).let { android ->
                project.afterEvaluate {
                    ServiceLoader.load(VariantProcessor::class.java, javaClass.classLoader)
                        .toList().let { processes ->
                            android.libraryVariants.forEach { variant ->
                                processes.forEach {
                                    it.process(variant)
                                }
                            }
                        }

                }
            }
        }
    }
}