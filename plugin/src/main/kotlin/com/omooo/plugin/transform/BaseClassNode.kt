package com.omooo.plugin.transform

import com.omooo.plugin.util.toPlainText
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode

internal class BaseClassNode(
    private val classVisitor: ClassVisitor,
    private val params: CheckParams
) : ClassNode(Opcodes.ASM9) {

    override fun visitEnd() {
        super.visitEnd()
        accept(classVisitor)
        transform()
    }

    private fun transform() {
        methods.forEach { methodNode ->
            methodNode.instructions.filterIsInstance<MethodInsnNode>().forEach { insnNode ->
                params.packageList.filter {
                    insnNode.owner.startsWith(it)
                }.forEach { _ ->
                    println(
                        """
                            检测到调用:
                                ----------------------------------------------------
                                $name#${methodNode.name}${methodNode.desc} 调用了 ${insnNode.toPlainText()}
                                ----------------------------------------------------
                        """.trimIndent()
                    )
                }
                params.methodList.filter {
                    insnNode.toPlainText() == it
                }.forEach {
                    println(
                        """
                            检测到调用:
                                ----------------------------------------------------
                                $name#${methodNode.name}${methodNode.desc} 调用了 $it}
                                ----------------------------------------------------
                        """.trimIndent()
                    )
                }
            }
        }
    }

}