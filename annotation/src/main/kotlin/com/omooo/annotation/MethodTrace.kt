package com.omooo.annotation

/**
 * Author: Omooo
 * Date: 2019/10/11
 * Version: v0.1.0
 * Desc: 方法耗时打点注解
 */

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class MethodTrace