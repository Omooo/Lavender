package com.omooo.plugin.transform.systrace

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.omooo.plugin.bean.ASM_VERSION
import com.omooo.plugin.util.*
import com.omooo.plugin.util.isInstanceMethod
import com.omooo.plugin.util.isMethodReturn
import com.omooo.plugin.util.isRClass
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import java.lang.reflect.Modifier

/**
 * Author: Omooo
 * Date: 2022/12/28
 * Desc: Systrace 自定义插桩
 */
abstract class SystraceCvFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        if (classContext.currentClassData.isRClass()
            || classContext.currentClassData.isSystemClass()
            || classContext.currentClassData.className.startsWith("org.bouncycastle")
        ) {
            return nextClassVisitor
        }
        return SystraceClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}