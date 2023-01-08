package com.omooo.plugin.util

import org.objectweb.asm.Opcodes
import java.lang.reflect.Modifier

/**
 * Author: Omooo
 * Date: 2022/12/28
 * Desc: Opcodes 扩展函数
 */

internal fun Int.isMethodReturn(): Boolean {
    return (this >= Opcodes.IRETURN && this <= Opcodes.RETURN)
            || this == Opcodes.ATHROW
}

/**
 * 是否是实例方法
 */
internal fun Int.isInstanceMethod(): Boolean {
    return !Modifier.isNative(this) && !Modifier.isInterface(this)
            && !Modifier.isAbstract(this)
}