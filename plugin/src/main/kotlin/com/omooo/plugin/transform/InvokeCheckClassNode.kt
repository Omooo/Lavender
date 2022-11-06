package com.omooo.plugin.transform

import com.omooo.plugin.util.toPlainText
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.MethodInsnNode

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 检测方法调用
 */
internal class InvokeCheckClassNode(
    classVisitor: ClassVisitor,
    private val params: CheckInvokeParams
) : BaseClassNode(classVisitor) {

    override fun transform() {
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