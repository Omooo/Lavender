package com.omooo.plugin.internal.cha

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * Author: Omooo
 * Date: 2023/3/13
 * Desc:
 */
internal class ReferenceAnalyser(
    private val entryPoints: Set<String>,
    private val classNodeMap: Map<String, ClassNode>,
) {

    fun analyze() {
        entryPoints.forEach { entryPoint ->
            classNodeMap[entryPoint]?.methods?.forEach { methodNode ->
                methodNode.instructions.forEach { absInsnNode ->
                    when (absInsnNode) {
                        is MethodInsnNode -> {
                            absInsnNode.name
                        }
                        is FieldInsnNode -> {
                            absInsnNode.name
                        }
                    }
                }
            }
        }
    }

    private fun analyzeInternal(isMethod: Boolean, owner: String, name: String, desc: String) {
        if (isMethod) {
            classNodeMap[owner]
        } else {

        }
    }

}