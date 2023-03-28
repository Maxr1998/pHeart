package edu.uaux.pheart.all_results

import java.time.Instant

class MeasurementDummyRepository {

    companion object {
        val allMeasurements = listOf(
            Measurement(72, Instant.parse("2021-12-03T10:15:30.00Z")),
            Measurement(108, Instant.parse("2021-12-03T14:15:30.00Z")),
            Measurement(64, Instant.parse("2022-01-02T09:15:00.00Z")),
            Measurement(89, Instant.parse("2022-01-02T10:15:30.00Z")),
            Measurement(72, Instant.parse("2022-12-03T10:15:30.00Z")),
            Measurement(108, Instant.parse("2022-12-03T14:15:30.00Z")),
            Measurement(64, Instant.parse("2023-01-02T09:15:00.00Z")),
            Measurement(89, Instant.parse("2023-01-02T10:15:30.00Z")),
            Measurement(72, Instant.parse("2023-02-10T10:15:30.00Z")),
            Measurement(108, Instant.parse("2023-02-10T14:15:30.00Z")),
            Measurement(64, Instant.parse("2023-01-02T12:15:00.00Z")),
            Measurement(89, Instant.parse("2023-01-02T18:15:30.00Z")),
            Measurement(72, Instant.parse("2021-12-03T10:15:30.00Z")),
            Measurement(108, Instant.parse("2021-12-03T14:15:30.00Z")),
            Measurement(64, Instant.parse("2022-01-02T09:15:00.00Z")),
            Measurement(89, Instant.parse("2022-01-02T10:15:30.00Z")),
            Measurement(72, Instant.parse("2022-12-03T10:15:30.00Z")),
            Measurement(108, Instant.parse("2022-12-03T14:15:30.00Z")),
            Measurement(64, Instant.parse("2023-01-02T09:15:00.00Z")),
            Measurement(89, Instant.parse("2023-01-02T10:15:30.00Z")),
            Measurement(72, Instant.parse("2023-02-10T10:15:30.00Z")),
            Measurement(108, Instant.parse("2023-02-10T14:15:30.00Z")),
            Measurement(64, Instant.parse("2023-01-02T12:15:00.00Z")),
            Measurement(89, Instant.parse("2023-01-02T18:15:30.00Z")),
        )

        val allMeasurementsDescending = allMeasurements.sortedByDescending { it.date }
    }
}