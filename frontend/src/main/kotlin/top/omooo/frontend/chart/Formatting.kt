package top.omooo.frontend.chart

const val BYTE_FACTOR = 1024
const val PERCENT_FACTOR = 100

fun formatSize(bytes: Number): String {
    val units = mutableListOf("B", "KB", "MB", "GB", "TB", "PB")
    var remainder = bytes.toDouble()
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
