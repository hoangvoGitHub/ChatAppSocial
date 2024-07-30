package com.hoangkotlin.chatappsocial.feature.auth

object AuthFormUtils {
    const val PASSWORD_MAX_LENGTH = 30
    const val PASSWORD_MIN_LENGTH = 10
    const val NAME_REGEX = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$"
    const val EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(.[A-Za-z0-9-]+)*(.[A-Za-z]{2,})$"

}

fun String.emailValidate(): String? {
    val emailRegex: Regex = AuthFormUtils.EMAIL_REGEX.toRegex()
    if (!emailRegex.containsMatchIn(this)) {
        return "Invalid email"
    }
    return null
}

fun String.passwordValidate(): String? {
    if (this.length < AuthFormUtils.PASSWORD_MIN_LENGTH) {
        return PasswordError.TooShort.text
    }
    if (this.length > AuthFormUtils.PASSWORD_MAX_LENGTH)
        return PasswordError.TooLong.text
    return null
}

fun String.passwordValidate(other: String): String? {
    if (this != other) {
        return PasswordError.NotMatch.text
    }
    return null
}

fun String.validateName(): String? {
    val nameRegex: Regex = AuthFormUtils.NAME_REGEX.toRegex()
    if (!nameRegex.containsMatchIn(this)) {
        return "Invalid name"
    }
    return null
}


enum class PasswordError(val text: String) {
    TooShort("Password is too short"),
    TooLong("Password is too long"),
    NotMatch("Password not match")
}