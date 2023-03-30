package edu.uaux.pheart.measure

enum class MeasureState {
    /**
     * Initial state, nothing is happening yet.
     */
    NONE,

    /**
     * We have the necessary permissions and can start the camera (including the preview).
     */
    IDLE,

    /**
     * The measuring process has started.
     */
    MEASURING,

    /**
     * Measuring is done, the user can review save the measurement.
     */
    FINISHED,
    ;
}