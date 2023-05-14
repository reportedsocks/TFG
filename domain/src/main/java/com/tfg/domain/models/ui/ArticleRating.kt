package com.tfg.domain.models.ui

enum class ArticleRating(val rating: Int) {
    EXCELLENT(5), GOOD(4), ADEQUATE(3), BAD(2), TERRIBLE(1);

    companion object {
        fun getByNum(num: Int): ArticleRating {
            return when(num) {
                1 -> TERRIBLE
                2 -> BAD
                3 -> ADEQUATE
                4 -> GOOD
                else -> EXCELLENT
            }
        }
    }
}