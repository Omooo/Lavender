package com.omooo.plugin.transform

import com.omooo.plugin.bean.ASM_VERSION
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.ClassNode

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 使用 [ClassNode] 操作字节码基础类
 */
internal abstract class BaseClassNode(private val classVisitor: ClassVisitor) :
    ClassNode(ASM_VERSION) {

    override fun visitEnd() {
        super.visitEnd()
        accept(classVisitor)
        transform()
    }

    abstract fun transform()
}