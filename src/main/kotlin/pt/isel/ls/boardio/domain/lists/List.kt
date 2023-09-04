package pt.isel.ls.boardio.domain.lists

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.services.utils.validateIndex
import pt.isel.ls.boardio.app.services.utils.validateString
import pt.isel.ls.boardio.app.utils.now
import pt.isel.ls.boardio.domain.Domain

@Serializable
data class List(
    override val id: Int,
    override val name: String,
    val boardId: Int,
    val index: Int,
    val archived: Boolean = false,
    override val createdDate: LocalDateTime = now()
) : Domain {
    init {
        validateId(id) { "Invalid list id" }
        validateString(name) { "Invalid list name" }
        validateId(boardId) { "Invalid board id" }
        validateIndex(index) { "Invalid index" }
    }
}
