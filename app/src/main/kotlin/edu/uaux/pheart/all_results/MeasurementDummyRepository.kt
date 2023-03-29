package edu.uaux.pheart.all_results

import edu.uaux.pheart.database.ActivityLevel
import edu.uaux.pheart.database.Measurement
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

object MeasurementDummyRepository {

    fun generateMeasurements(number: Int = 50): List<Measurement> {
        val startTime = ZonedDateTime.now(ZoneId.systemDefault())

        return buildList {
            repeat(number) {
                add(
                    Measurement(
                        startTime.minusMinutes((it * 30).toLong()).scattered(20),
                        randomBpm(),
                        randomActivityLevel(),
                    ),
                )
            }
        }.distinctBy { it.timestamp }.sortedByDescending { it.timestamp }
    }

    private fun randomBpm() = Random.nextInt(35, 125)
    private fun randomActivityLevel() = when (Random.nextInt(0, 4)) {
        0 -> ActivityLevel.RELAXED
        1 -> ActivityLevel.SEATED
        2 -> ActivityLevel.LIGHT_EXERCISE
        3 -> ActivityLevel.HEAVY_EXERCISE
        else -> ActivityLevel.RELAXED
    }

    private fun ZonedDateTime.scattered(minutes: Int): ZonedDateTime {
        return plusMinutes((Random.nextFloat() * (minutes * 2) - minutes).toLong())
    }
}