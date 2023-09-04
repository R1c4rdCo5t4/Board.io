package pt.isel.ls.boardio.domain.cards.database

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.boardio.app.database.database.checkIfNotExists
import pt.isel.ls.boardio.app.database.database.execute
import pt.isel.ls.boardio.app.database.database.executeQuery
import pt.isel.ls.boardio.app.database.database.getCard
import pt.isel.ls.boardio.app.database.database.getNewCardIndex
import pt.isel.ls.boardio.app.database.database.getNextIndex
import pt.isel.ls.boardio.app.database.database.getValuesToUpdate
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.domain.cards.Card

class CardsDatabase : CardsSource {

    override fun createCard(source: Source, name: String, description: String, listId: Int): Int {
        source.conn.checkIfNotExists("list", "id", listId) { "List with id $listId not found" }
        val index = source.conn.getNextIndex("index", "card", "where listId = $listId")
        val stm = "insert into card(name, description, listId, dueDate, index) values (?,?,?,?,?) returning id"
        val args = listOf(name, description, listId, null, index)
        val column = listOf("id")
        val (cardId) = source.conn.executeQuery(stm, args, column)
        return cardId.toInt()
    }

    override fun getCard(source: Source, cardId: Int): Card {
        val stm = "select id, name, description, listId, index, dueDate, archived, createdDate from card where id = ?"
        val args = listOf(cardId)
        val query = source.conn.executeQuery(stm, args) { "Card with id $cardId not found" }
        return source.conn.getCard(query)
    }

    override fun moveCard(source: Source, cardId: Int, listId: Int, index: Int) {
        source.conn.checkIfNotExists("card", "id", cardId) { "Card with id $cardId not found" }
        source.conn.checkIfNotExists("list", "id", listId) { "List with id $listId not found" }
        val destIndex = source.conn.getNewCardIndex(index, cardId, listId)
        val updateStm = "update card set index = ?, listId = ? where id = ?"
        val updateArgs = listOf(destIndex, listId, cardId)
        source.conn.execute(updateStm, updateArgs)
    }

    override fun getListIdOfCard(source: Source, cardId: Int): Int {
        val stm = "select listId from card where id = ?"
        val args = listOf(cardId)
        val (listId) = source.conn.executeQuery(stm, args) { "Card with id $cardId not found" }
        return listId.toInt()
    }

    override fun deleteCard(source: Source, cardId: Int) {
        val stm = "delete from card where id = ?"
        val arg = listOf(cardId)
        source.conn.execute(stm, arg)
    }

    override fun updateCard(source: Source, cardId: Int, name: String?, description: String?, archived: Boolean?) {
        val (columns, valuesToUpdate) = getValuesToUpdate(
            "name" to name,
            "description" to description,
            "archived" to archived
        )
        val stm = "update card set $columns where id = ?"
        val args = listOf(*valuesToUpdate, cardId)
        source.conn.execute(stm, args)
    }

    override fun updateCardDueDate(source: Source, cardId: Int, dueDate: LocalDateTime?) {
        val stm = "update card set dueDate = ? where id = ?"
        val args = listOf(dueDate, cardId)
        source.conn.execute(stm, args)
    }
}
