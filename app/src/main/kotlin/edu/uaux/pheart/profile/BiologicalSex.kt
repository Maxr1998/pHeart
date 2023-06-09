package edu.uaux.pheart.profile

enum class BiologicalSex(val id: Int) {
    NONE(0), // for more consistent persistence handling
    MALE(1),
    FEMALE(2),
    ;

    companion object {
        fun fromId(id: Int) = BiologicalSex.values().firstOrNull { it.id == id } ?: NONE
    }
}