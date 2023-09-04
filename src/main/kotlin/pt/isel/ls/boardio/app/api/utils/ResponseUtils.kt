package pt.isel.ls.boardio.app.api.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Response
import java.sql.SQLException

@Serializable
data class ResponseMessage(val message: String)

@Serializable
data class ResponseError(val error: String)

fun Response.message(message: String) = json(ResponseMessage(message))

fun Response.error(e: Exception): Response {
    val description = when (e) {
        is SQLException -> "Database Error"
        else -> status.description
    }
    return json(ResponseError(description))
}

inline fun <reified T> Response.json(body: T) =
    header("content-type", "application/json").body(Json.encodeToString(body))

inline fun <reified T> Response.decodeAs(): T = Json.decodeFromString(bodyString())
