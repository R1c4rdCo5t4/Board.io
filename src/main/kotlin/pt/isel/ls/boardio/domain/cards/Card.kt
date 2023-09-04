package pt.isel.ls.boardio.domain.cards

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import pt.isel.ls.boardio.app.services.utils.validateCreatedDate
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.services.utils.validateIndex
import pt.isel.ls.boardio.app.services.utils.validateString
import pt.isel.ls.boardio.app.utils.now
import pt.isel.ls.boardio.domain.Domain

@Serializable
data class Card(
    override val id: Int,
    override val name: String,
    val description: String,
    val listId: Int,
    val index: Int,
    val dueDate: LocalDateTime? = null,
    val archived: Boolean = false,
    override val createdDate: LocalDateTime = now()
) : Domain {
    init {
        validateId(id) { "Invalid card id" }
        validateString(name) { "Invalid card name" }
        validateId(listId) { "Invalid list id" }
        validateIndex(index) { "Invalid index" }
        validateCreatedDate(createdDate) { "Invalid date of creation" }
    }
}
