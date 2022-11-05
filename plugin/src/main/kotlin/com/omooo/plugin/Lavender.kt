package com.omooo.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.omooo.plugin.ext.Convert2WebpExtension
import com.omooo.plugin.spi.VariantProcessor
import com.omooo.plugin.transform.CommonClassVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

/**
 * Author: Omooo
 * Date: 2019/9/27
 * Version: v0.1.0
 * Desc: Plugin
 */
open class Lavender : Plugin<Project> {

    override fun apply(project: Project) {

        println("apply plugin: 'Lavender'")

        val androidExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidExtension.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                CommonClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) {

            }
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }


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