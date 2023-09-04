package pt.isel.ls.boardio.app.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun hashPassword(password: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(password.toByteArray(StandardCharsets.UTF_8))
    val hexString = StringBuilder()
    for (byte in hashBytes) {
        val hex = String.format("%02x", byte.toInt() and 0xFF)
        hexString.append(hex)
    }
    return hexString.toString()
}

fun verifyPassword(password: String, hashedPassword: String): Boolean = hashPassword(password) == hashedPassword
