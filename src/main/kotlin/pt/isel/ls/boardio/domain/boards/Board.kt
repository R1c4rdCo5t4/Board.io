package pt.isel.ls.boardio.domain.boards

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.services.utils.validateString
import pt.isel.ls.boardio.app.utils.now
import pt.isel.ls.boardio.domain.Domain

@Serializable
data class Board(
    override val id: Int,
    override val name: String,
    val description: String,
    override val createdDate: LocalDateTime = now()
) : Domain {
    init {
        validateId(id) { "Invalid board id" }
        validateString(name) { "Invalid board name" }
    }
}
