package edu.uaux.pheart.statistics

import edu.uaux.pheart.database.Measurement
import edu.uaux.pheart.database.MeasurementDao

suspend fun MeasurementDao.get(range: ZonedDateTimeRange): List<Measurement> {
    return getAll(range.start, range.end)
}