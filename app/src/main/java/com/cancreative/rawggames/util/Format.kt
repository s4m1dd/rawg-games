package com.cancreative.rawggames.util

private val MONTHS = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

/** 2017-11-24 -> 24 Nov 2017. Unparseable input comes back as-is. */
fun formatReleaseDate(iso: String?): String {
    val parts = iso?.split("-") ?: return ""
    if (parts.size != 3) return iso
    val month = parts[1].toIntOrNull()?.takeIf { it in 1..12 } ?: return iso
    val day = parts[2].toIntOrNull() ?: return iso
    return "$day ${MONTHS[month - 1]} ${parts[0]}"
}
