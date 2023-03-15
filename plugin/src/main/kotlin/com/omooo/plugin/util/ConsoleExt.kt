package com.omooo.plugin.util

/**
 * Author: Omooo
 * Date: 2023/3/12
 * Desc: 日志输出文本颜色设置
 */

private const val ESC = '\u001B'
private const val CSI_RESET = "$ESC[0m"
private const val CSI_RED = "$ESC[31m"
private const val CSI_GREEN = "$ESC[32m"
private const val CSI_YELLOW = "$ESC[33m"

internal fun red(s: Any) = "${CSI_RED}${s}${CSI_RESET}"
internal fun green(s: Any) = "${CSI_GREEN}${s}${CSI_RESET}"
internal fun yellow(s: Any) = "${CSI_YELLOW}${s}${CSI_RESET}"



