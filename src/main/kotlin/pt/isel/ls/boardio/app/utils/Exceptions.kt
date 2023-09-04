package pt.isel.ls.boardio.app.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED

sealed class ApplicationException(override val message: String) : Exception(message)
class NotFoundException(override val message: String) : ApplicationException(message)
class AlreadyExistsException(override val message: String) : ApplicationException(message)
class UnauthorizedException(override val message: String) : ApplicationException(message)
class ForbiddenException(override val message: String) : ApplicationException(message)

@OptIn(ExperimentalSerializationApi::class)
fun Exception.status(): Status {
    val status = when (this) {
        is IllegalArgumentException -> BAD_REQUEST
        is NotFoundException -> NOT_FOUND
        is AlreadyExistsException -> CONFLICT
        is UnauthorizedException -> UNAUTHORIZED
        is ForbiddenException -> FORBIDDEN
        else -> INTERNAL_SERVER_ERROR
    }
    val message = when (this) {
        is MissingFieldException -> "Missing Fields: ${this.missingFields.joinToString()}"
        is SerializationException -> "Invalid Body"
        else -> this.message
    }
    return Status(status.code, message)
}
