package com.omooo.plugin.transform

import com.omooo.plugin.bean.ASM_VERSION
import org.objectweb.asm.ClassVisitor

/**
 * Author: Omooo
 * Date: 2022/11/6
 * Desc: 使用 [ClassVisitor] 操作字节码基础类
 */
internal abstract class BaseClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(ASM_VERSION, classVisitor) {

}