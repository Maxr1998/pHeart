package edu.uaux.pheart.database

import edu.uaux.pheart.statistics.units.ZonedDateTimeRange

/**
 * @see MeasurementDao.getAll
 */
suspend fun MeasurementDao.get(range: ZonedDateTimeRange): List<Measurement> {
    return getAll(range.start, range.end)
}