package pt.isel.ls.boardio.domain.cards.database

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.boardio.app.database.datamem.getNextId
import pt.isel.ls.boardio.app.database.datamem.getNextIndex
import pt.isel.ls.boardio.app.database.datamem.updateCardIndices
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.domain.cards.Card

class CardsDataMem : CardsSource {
    override fun createCard(source: Source, name: String, description: String, listId: Int): Int {
        val cardId = source.mem.cards.keys.getNextId()
        val cardIndex = source.mem.cards.keys.getNextIndex()
        source.mem.cards[cardId] = Card(cardId, name, description, listId, cardIndex)
        return cardId
    }

    override fun getCard(source: Source, cardId: Int): Card {
        return source.mem.cards[cardId] ?: throw NotFoundException("Card with id $cardId not found")
    }

    override fun moveCard(source: Source, cardId: Int, listId: Int, index: Int) {
        if (!source.mem.lists.containsKey(listId)) throw NotFoundException("List with id $listId was not found")
        val maxIdx = source.mem.lists.values.map { it.index }.getNextIndex()
        require(index <= maxIdx) { "Index $index is out of bounds" }
        val card = source.mem.cards[cardId] ?: throw NotFoundException("Card with id $cardId was not found")
        source.mem.cards[cardId] = card.copy(index = index, listId = listId)
        source.mem.updateCardIndices(cardId, listId)
    }

    override fun getListIdOfCard(source: Source, cardId: Int): Int {
        return source.mem.cards[cardId]?.listId ?: throw NotFoundException("Card with id $cardId was not found")
    }

    override fun deleteCard(source: Source, cardId: Int) {
        if (!source.mem.cards.containsKey(cardId)) throw NotFoundException("Card with id $cardId was not found")
        source.mem.cards.remove(cardId)
    }

    override fun updateCard(source: Source, cardId: Int, name: String?, description: String?, archived: Boolean?) {
        val card = source.mem.cards[cardId] ?: throw NotFoundException("Card with id $cardId was not found")
        source.mem.cards[cardId] =
            card.copy(
                name = name ?: card.name,
                description = description ?: card.description,
                archived = archived ?: card.archived
            )
    }

    override fun updateCardDueDate(source: Source, cardId: Int, dueDate: LocalDateTime?) {
        val card = source.mem.cards[cardId] ?: throw NotFoundException("Card with id $cardId was not found")
        source.mem.cards[cardId] = card.copy(dueDate = dueDate)
    }
}
