package com.antsyferov.tfg.util

object ValidationUtils {
    val EMAIL_PATTERN = "^[a-zA-Z0-9!?#\$%^&*_+=.-]{2,60}@[a-zA-Z0-9-]{2,20}.[a-zA-Z]{2,5}\$".toRegex()
    val NAME_LENGTH_PATTERN = "^.{2,60}$".toRegex()
    val SPECIAL_CHARACTERS_PATTERN = "^([a-zA-Zñéúíóá'‘’\\- ](?!.*'{2})(?!.*‘{2})(?!.*’{2})(?!.*[\\- ]{2}))*\$".toRegex()
}