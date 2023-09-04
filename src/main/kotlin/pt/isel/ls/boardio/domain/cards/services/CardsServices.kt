package pt.isel.ls.boardio.domain.cards.services

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.boardio.app.database.Database
import pt.isel.ls.boardio.app.services.utils.validateDescription
import pt.isel.ls.boardio.app.services.utils.validateDueDate
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.services.utils.validateIndex
import pt.isel.ls.boardio.app.services.utils.validateName
import pt.isel.ls.boardio.app.services.utils.validateToken
import pt.isel.ls.boardio.domain.cards.Card

class CardsServices(private val db: Database) {

    fun createCard(token: String, name: String, description: String, listId: Int): Int {
        validateName(name) { "Invalid card name" }
        validateId(listId) { "Invalid list id" }
        validateToken(token) { "Invalid user token" }
        validateDescription(description) { "Invalid description" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.cards.createCard(it, name, description, listId)
        }
    }

    fun getCard(token: String, cardId: Int): Card {
        validateId(cardId) { "Invalid card id" }
        validateToken(token) { "Invalid user token" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val listId = db.cards.getListIdOfCard(it, cardId)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.cards.getCard(it, cardId)
        }
    }

    fun moveCard(token: String, cardId: Int, destListId: Int, index: Int) {
        validateId(cardId) { "Invalid card id" }
        validateId(destListId) { "Invalid list id" }
        validateToken(token) { "Invalid user token" }
        validateIndex(index) { "Invalid card index" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val srcListId = db.cards.getListIdOfCard(it, cardId)
            val boardId = db.lists.getBoardIdOfList(it, srcListId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.cards.moveCard(it, cardId, destListId, index)
        }
    }

    fun deleteCard(token: String, cardId: Int) {
        validateId(cardId) { "Invalid card id" }
        validateToken(token) { "Invalid user token" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val listId = db.cards.getListIdOfCard(it, cardId)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.cards.deleteCard(it, cardId)
        }
    }

    fun updateCard(token: String, cardId: Int, name: String?, description: String?, archived: Boolean?) {
        validateToken(token) { "Invalid user token" }
        validateId(cardId) { "Invalid card id" }
        if (name != null) validateName(name) { "Invalid card name" }
        if (description != null) validateDescription(description) { "Invalid description" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val listId = db.cards.getListIdOfCard(it, cardId)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.cards.updateCard(it, cardId, name, description, archived)
        }
    }

    fun updateCardDueDate(token: String, cardId: Int, dueDate: LocalDateTime?) {
        validateToken(token) { "Invalid user token" }
        validateId(cardId) { "Invalid card id" }
        validateDueDate(dueDate) { "Invalid due date" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            val listId = db.cards.getListIdOfCard(it, cardId)
            val boardId = db.lists.getBoardIdOfList(it, listId)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.cards.updateCardDueDate(it, cardId, dueDate)
        }
    }
}
