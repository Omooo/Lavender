package com.omooo.plugin.internal.cha

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Author: Omooo
 * Date: 2023/3/12
 * Desc:
 */
internal class ClassSetCache(private val file: File) {

    private val classCacheMap: Map<String, ClassNode> by lazy {
        loadClasses(ZipInputStream(FileInputStream(file))).associateBy {
            it.name
        }
    }

    fun get(className: String): ClassNode? {
        return classCacheMap[className]
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
}