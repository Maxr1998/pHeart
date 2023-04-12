package edu.uaux.pheart.statistics.units

import java.time.ZonedDateTime

/**
 * Represents a range of [ZonedDateTime]s.
 */
data class ZonedDateTimeRange(val start: ZonedDateTime, val end: ZonedDateTime)