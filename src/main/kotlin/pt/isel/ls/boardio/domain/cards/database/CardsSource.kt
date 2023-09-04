package pt.isel.ls.boardio.domain.cards.database

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.domain.cards.Card

interface CardsSource {
    fun createCard(source: Source, name: String, description: String, listId: Int): Int
    fun getCard(source: Source, cardId: Int): Card
    fun moveCard(source: Source, cardId: Int, listId: Int, index: Int)
    fun getListIdOfCard(source: Source, cardId: Int): Int
    fun deleteCard(source: Source, cardId: Int)
    fun updateCard(source: Source, cardId: Int, name: String?, description: String?, archived: Boolean?)
    fun updateCardDueDate(source: Source, cardId: Int, dueDate: LocalDateTime?)
}
