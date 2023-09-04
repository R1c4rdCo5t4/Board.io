package pt.isel.ls.boardio.domain.lists.services

import pt.isel.ls.boardio.app.database.Database
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.services.utils.validateIndex
import pt.isel.ls.boardio.app.services.utils.validateName
import pt.isel.ls.boardio.app.services.utils.validatePositiveInt
import pt.isel.ls.boardio.app.services.utils.validateToken
import pt.isel.ls.boardio.domain.cards.Card
import pt.isel.ls.boardio.domain.lists.List

class ListsServices(private val db: Database) {

    fun createList(token: String, name: String, boardId: Int): Int {
        validateName(name) { "Invalid list name" }
        validateId(boardId) { "Invalid board id" }
        validateToken(token) { "Invalid user token" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.lists.createList(it, name, boardId)
        }
    }

    fun getList(token: String, listId: Int): List {
        validateId(listId) { "Invalid list id" }
        validateToken(token) { "Invalid user token" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.lists.getList(it, listId)
        }
    }

    fun getListCards(token: String, listId: Int, skip: Int? = null, limit: Int? = null): kotlin.collections.List<Card> {
        validateId(listId) { "Invalid list id" }
        validateToken(token) { "Invalid user token" }
        validatePositiveInt(limit) { "Limit value cannot be negative" }
        validatePositiveInt(skip) { "Skip value cannot be negative" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.lists.getListCards(it, listId, skip, limit)
        }
    }

    fun deleteList(token: String, listId: Int) {
        validateId(listId) { "Invalid list id" }
        validateToken(token) { "Invalid user token" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.lists.deleteList(it, listId)
        }
    }

    fun moveList(token: String, listId: Int, index: Int) {
        validateId(listId) { "Invalid list id" }
        validateToken(token) { "Invalid user token" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.lists.moveList(it, listId, index)
        }
    }

    fun updateList(token: String, listId: Int, name: String?, index: Int?, archived: Boolean?) {
        validateToken(token) { "Invalid user token" }
        validateId(listId) { "Invalid list id" }
        if (index != null) validateIndex(index) { "Invalid index" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.lists.updateList(it, listId, name, index, archived)
        }
    }
}
