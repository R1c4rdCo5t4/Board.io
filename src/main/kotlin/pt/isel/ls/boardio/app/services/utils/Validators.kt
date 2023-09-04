package pt.isel.ls.boardio.app.services.utils

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.boardio.app.utils.after
import pt.isel.ls.boardio.app.utils.before
import pt.isel.ls.boardio.app.utils.now

const val MIN_ID = 1
const val MIN_INDEX = 0
const val TOKEN_LENGTH = 36
const val MAX_NAME_SIZE = 30
const val MAX_EMAIL_SIZE = 30
const val MAX_DESCRIPTION_SIZE = 50

fun validateId(number: Int, message: () -> String = { "" }): Boolean {
    return validate(number < MIN_ID, message)
}

fun validateIndex(index: Int, message: () -> String = { "" }): Boolean {
    return validate(index < MIN_INDEX, message)
}

fun validatePositiveInt(number: Int?, message: () -> String = { "" }): Boolean {
    if (number == null) return true
    return validate(number < 0, message)
}

fun validateString(str: String, message: () -> String = { "" }): Boolean {
    return validate(str.isEmpty(), message)
}

fun validateName(name: String, message: () -> String = { "" }): Boolean {
    validateString(name, message)
    return validate(name.length >= MAX_NAME_SIZE) { "Name is too long" }
}

fun validateDescription(description: String, message: () -> String = { "" }): Boolean {
    validateString(description, message)
    return validate(description.length >= MAX_DESCRIPTION_SIZE) { "Description is too long" }
}

fun validateToken(token: String, message: () -> String = { "" }): Boolean {
    return validate(token.length != TOKEN_LENGTH, message)
}

fun validateEmail(email: String, message: () -> String = { "" }): Boolean {
    validateString(email, message)
    validate(email.length >= MAX_EMAIL_SIZE) { "Email is too long" }
    return validate(!email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$")), message)
}

fun validateCreatedDate(date: LocalDateTime, message: () -> String = { "" }): Boolean {
    return validate(date.after(now()), message)
}

fun validateDueDate(date: LocalDateTime?, createdDate: LocalDateTime = now(), message: () -> String = { "" }): Boolean {
    if (date == null) return true
    return validate(date.before(createdDate), message)
}

fun validate(condition: Boolean, message: () -> String): Boolean {
    require(!condition, message)
    return true
}
