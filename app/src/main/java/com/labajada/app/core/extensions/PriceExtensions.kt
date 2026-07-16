package com.labajada.app.core.extensions

fun String.toPrecioDouble(): Double {
    return this
        .replace("S/.", "")
        .replace("S/ ", "")
        .trim()
        .toDoubleOrNull() ?: 0.0
}