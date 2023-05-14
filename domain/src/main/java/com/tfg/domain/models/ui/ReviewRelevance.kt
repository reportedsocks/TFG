package com.tfg.domain.models.ui

enum class ReviewRelevance(val num: Int) {
    EXPERT(5), CONFIDENT(4), INTERMEDIATE(3), SUPERFICIAL(2), NOVICE(1);

    companion object {
        fun getByNum(num: Int): ReviewRelevance {
            return when(num) {
                1 -> NOVICE
                2 -> SUPERFICIAL
                3 -> INTERMEDIATE
                4 -> CONFIDENT
                else -> EXPERT
            }
        }
    }
}