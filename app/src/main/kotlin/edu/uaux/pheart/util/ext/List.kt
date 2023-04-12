package edu.uaux.pheart.util.ext

import kotlin.math.roundToInt

fun <T> List<T>.avgOf(selector: (T) -> Int): Int {
    if (isEmpty()) throw NoSuchElementException()
    return (sumOf(selector) / size.toDouble()).roundToInt()
}

fun <T> List<T>.avgOfOrNull(selector: (T) -> Int): Int? {
    if (isEmpty()) return null
    return (sumOf(selector) / size.toDouble()).roundToInt()
}