package edu.uaux.pheart.all_results

import java.time.Instant

data class Measurement(
    val bpm: Int,
    val date: Long,
) {
    val dateInstant: Instant
        get() = Instant.ofEpochMilli(date)

    constructor(bpm: Int, date: Instant) : this(bpm, date.toEpochMilli())
}