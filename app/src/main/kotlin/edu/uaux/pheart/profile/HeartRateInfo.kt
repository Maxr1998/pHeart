package edu.uaux.pheart.profile

import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Used for querying the different recommended heart rate ranges.
 */
object HeartRateInfo {
    /**
     * @return the recommended resting heart rate range for a given age and sex
     */
    fun getRestingHeartRate(age: Int, biologicalSex: BiologicalSex): IntRange {
        require(age in 0..Int.MAX_VALUE)
        val map = when (biologicalSex) {
            BiologicalSex.NONE -> maleResting
            BiologicalSex.MALE -> maleResting
            BiologicalSex.FEMALE -> femaleResting
        }
        return map.firstNotNullOf { (ageRange, heartRateRange) ->
            if (age in ageRange) {
                heartRateRange
            } else {
                null
            }
        }
    }

    /**
     * @return the approximate maximum heart rate for a given age and sex
     */
    fun getMaxHeartRate(age: Int, biologicalSex: BiologicalSex): Int {
        require(age in 0..Int.MAX_VALUE)
        return 220 - min(age, 150) + when (biologicalSex) {
            BiologicalSex.NONE,
            BiologicalSex.MALE,
            -> 0
            BiologicalSex.FEMALE -> 7 // female heart rate higher by 7 on average
        }
    }

    /**
     * @return the recommended exercise heart rate range for a given age and sex
     */
    fun getExerciseHeartRange(age: Int, biologicalSex: BiologicalSex): IntRange {
        require(age in 0..Int.MAX_VALUE)
        val maxHr = getMaxHeartRate(age, biologicalSex)
        return (maxHr * 0.64).roundToInt()..(maxHr * 0.76).roundToInt() // recommended heart range 64-76% of max range
    }

    private val ageGroups: List<IntRange> = listOf(
        0..17,
        18..25,
        26..35,
        36..45,
        46..55,
        56..65,
        65..Int.MAX_VALUE,
    )

    private val maleResting: Map<IntRange, IntRange> = buildMap {
        put(ageGroups[0], 55..85)
        put(ageGroups[1], 62..73)
        put(ageGroups[2], 62..74)
        put(ageGroups[3], 63..75)
        put(ageGroups[4], 64..76)
        put(ageGroups[5], 62..75)
        put(ageGroups[6], 62..73)
    }

    private val femaleResting: Map<IntRange, IntRange> = buildMap {
        put(ageGroups[0], 55..85)
        put(ageGroups[1], 62..78)
        put(ageGroups[2], 65..76)
        put(ageGroups[3], 65..78)
        put(ageGroups[4], 66..77)
        put(ageGroups[5], 65..77)
        put(ageGroups[6], 65..76)
    }
}