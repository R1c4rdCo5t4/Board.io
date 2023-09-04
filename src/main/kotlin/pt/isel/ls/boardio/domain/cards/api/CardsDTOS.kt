package pt.isel.ls.boardio.domain.cards.api

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CreateCardRequest(val name: String, val description: String, val listId: Int)

@Serializable
data class CreateCardResponse(val id: Int)

@Serializable
data class UpdateCardRequest(val name: String? = null, val description: String? = null, val archived: Boolean? = null)

@Serializable
data class UpdateCardDueDateRequest(val dueDate: LocalDateTime? = null)
