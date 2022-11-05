package com.omooo.plugin.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

internal class BaseClassNode(val classVisitor: ClassVisitor) : ClassNode(Opcodes.ASM9) {
    override fun visitEnd() {
        super.visitEnd()
        accept(classVisitor)
        println("BaseClassNode: $name")
    }
}