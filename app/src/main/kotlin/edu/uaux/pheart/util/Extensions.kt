package edu.uaux.pheart.util

import kotlin.math.roundToInt

fun <T> List<T>.avgOf(selector: (T) -> Int): Int = (sumOf(selector) / size.toDouble()).roundToInt()