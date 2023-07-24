package top.omooo.frontend.util

/**
 * Author: Omooo
 * Date: 2023/2/3
 * Desc: 数字相关格式化
 */

/**
 * 格式化数字
 *
 * ag: 21578 字节 -> 21.1 KB
 */
fun Number.formatSize(): String {
    val units = mutableListOf("B", "KB", "MB", "GB", "TB", "PB")
    var remainder = this.toDouble()
    while (remainder > BYTE_FACTOR) {
        remainder /= BYTE_FACTOR
        units.removeFirst()
    }
    return "${remainder.asDynamic().toFixed(1)} ${units.first()}"
}

fun formatPercentage(fraction: Number, total: Number): String {
    val percentage = PERCENT_FACTOR * fraction.toDouble() / total.toDouble()
    return "${percentage.asDynamic().toFixed(2)} %"
}

private const val BYTE_FACTOR = 1024

private const val PERCENT_FACTOR = 100