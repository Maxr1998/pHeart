package edu.uaux.pheart.util

fun fftFreq(n: Int, d: Double = 1.0): DoubleArray {
    require(n > 0) { "n must be positive" }
    require(d > 0) { "d must be positive" }
    val result = DoubleArray(n)
    val f = 1.0 / (n * d)
    val m = n / 2 + 1
    for (i in 0 until m) {
        result[i] = i * f
    }
    for (i in m until n) {
        result[i] = (i - n) * f
    }
    return result
}