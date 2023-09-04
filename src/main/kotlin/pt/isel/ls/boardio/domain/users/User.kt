package pt.isel.ls.boardio.domain.users

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.services.utils.validateString
import pt.isel.ls.boardio.app.utils.now
import pt.isel.ls.boardio.domain.Domain

@Serializable
data class User(
    override val id: Int,
    override val name: String,
    val email: String,
    val token: String,
    val password: String?,
    override val createdDate: LocalDateTime = now()
) : Domain {
    init {
        validateId(id) { "Invalid user id" }
        validateString(name) { "Invalid username" }
        validateString(email) { "Invalid e-mail" }
        validateString(token) { "Invalid token" }
        if (password != null) validateString(password) { "Invalid password" }
    }
}
