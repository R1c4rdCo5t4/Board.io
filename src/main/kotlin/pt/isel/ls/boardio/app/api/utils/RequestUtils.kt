package pt.isel.ls.boardio.app.api.utils

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.routing.path
import pt.isel.ls.boardio.app.utils.UnauthorizedException

fun Request.getParameter(name: String) = path(name)
    ?: throw IllegalArgumentException("Missing parameter '$name'")

fun Request.getIntParameter(name: String) = getParameter(name).toIntOrNull()
    ?: throw IllegalArgumentException("Invalid $name")

fun Request.getOptionalParameter(name: String) = path(name)

fun Request.getOptionalIntParameter(name: String) = if (path(name) != null) getIntParameter(name) else null

fun Request.getQuery(name: String) = query(name) ?: throw IllegalArgumentException("Missing query '$name'")

fun Request.getIntQuery(name: String) = getQuery(name).toIntOrNull() ?: throw IllegalArgumentException("Invalid $name")

fun Request.getQueries(vararg names: String) = names.map { getQuery(it) }

fun Request.getIntQueries(vararg names: String) = names.map { getIntQuery(it) }

fun Request.getOptionalQuery(name: String) = query(name)

fun Request.getOptionalIntQuery(name: String) = if (query(name) != null) getIntQuery(name) else null

fun Request.getOptionalQueries(vararg names: String) = names.map { getOptionalQuery(it) }

fun Request.getOptionalIntQueries(vararg names: String) = names.map { getOptionalIntQuery(it) }

fun Request.setToken(token: String) = header("authorization", "bearer $token")

fun Request.getToken(): String {
    val token = header("authorization") ?: throw UnauthorizedException("Missing token")
    val start = "bearer "
    if (!token.startsWith(start, ignoreCase = true)) throw UnauthorizedException("Invalid token")
    return token.substring(start.length)
}

inline fun <reified T> Request.decodeAs(): T = Json.decodeFromString(bodyString())

fun Request.json(body: String) = header("content-type", "application/json").body(body)

fun Request.json(vararg properties: Pair<String, Any?>) = json(stringify(*properties))

private fun stringify(vararg properties: Pair<String, Any?>): String {
    val json = StringBuilder("{")
    properties.joinTo(json, separator = ", ") { (name, value) ->
        """"$name": ${parseValue(value)}"""
    }
    json.append("}")
    return json.toString()
}

private fun <T> parseValue(value: T?): String {
    return when (value) {
        null -> "null"
        is String -> "\"$value\""
        else -> value.toString()
    }
}
