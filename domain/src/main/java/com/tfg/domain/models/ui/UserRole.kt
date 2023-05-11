package com.tfg.domain.models.ui

enum class UserRole(val num: Int) {
    AUTHOR(0), REVIEWER(1), ADMIN(2);

    companion object {
        fun getByNumber(num: Int): UserRole {
            return when(num) {
                2 -> ADMIN
                1 -> REVIEWER
                else -> AUTHOR
            }
        }
    }
}