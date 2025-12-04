package com.edu.dam.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

const val USERNAME_MIN = 3
const val USERNAME_MAX = 30
private val USERNAME_REGEX = Regex("""^[\p{L}\p{N}_\- ]+${'$'}""")

data class UserNameValidation(
    val raw: String,
    val trimmed: String,
    val length: Int,
    val lengthAllowed: Boolean,
    val charsetAllowed: Boolean
){
    val isValid: Boolean = trimmed.isNotEmpty() && lengthAllowed && charsetAllowed
}

fun validateUserName(raw: String): UserNameValidation {
    val trimmed = raw.trim()
    val length = trimmed.length
    val lengthOk = length in USERNAME_MIN..USERNAME_MAX
    val charsetOk = trimmed.isEmpty() || USERNAME_REGEX.matches(trimmed)
    return UserNameValidation(
        raw = raw,
        trimmed = trimmed,
        length = length,
        lengthAllowed = lengthOk,
        charsetAllowed = charsetOk
    )
}

@Composable
fun UserNameSupportingText(validation: UserNameValidation){
    val count = "${validation.length} / $USERNAME_MAX"
    val message = when{
        validation.raw.isEmpty() -> count
        !validation.lengthAllowed -> "Entre $USERNAME_MIN y $USERNAME_MAX caracteres · $count"
        !validation.charsetAllowed -> "Solo Letras, Numeros, Espacios, _ y - · $count"
        else -> count
    }
    Text(message)
}
