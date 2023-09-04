package pt.isel.ls.boardio.domain.boards.services

import pt.isel.ls.boardio.app.database.Database
import pt.isel.ls.boardio.app.services.utils.validateDescription
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.services.utils.validateName
import pt.isel.ls.boardio.app.services.utils.validatePositiveInt
import pt.isel.ls.boardio.app.services.utils.validateToken
import pt.isel.ls.boardio.domain.boards.Board
import pt.isel.ls.boardio.domain.lists.List
import pt.isel.ls.boardio.domain.users.User

class BoardsServices(private val db: Database) {

    fun createBoard(token: String, name: String, description: String): Int {
        validateName(name) { "Invalid board name" }
        validateToken(token) { "Invalid user token" }
        validateDescription(description) { "Invalid board description" }
        return db.fetch {
            db.boards.createBoard(it, name, description).also { boardId ->
                db.boards.addUserToBoard(it, it.authenticateUser(token), boardId)
            }
        }
    }

    fun getBoard(token: String, boardId: Int): Board {
        validateId(boardId) { "Invalid board id" }
        validateToken(token) { "Invalid user token" }
        return db.fetch {
            it.authenticateUser(token)
            val userId = it.authenticateUser(token)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.boards.getBoard(it, boardId)
        }
    }

    fun getUserBoards(token: String, skip: Int? = null, limit: Int? = null): kotlin.collections.List<Board> {
        validateToken(token) { "Invalid user token" }
        validatePositiveInt(limit) { "Limit value cannot be negative" }
        validatePositiveInt(skip) { "Skip value cannot be negative" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            db.boards.getUserBoards(it, userId, skip, limit)
        }
    }

    fun getBoardLists(token: String, boardId: Int, skip: Int? = null, limit: Int? = null): kotlin.collections.List<List> {
        validateId(boardId) { "Invalid board id" }
        validateToken(token) { "Invalid user token" }
        validatePositiveInt(limit) { "Limit value cannot be negative" }
        validatePositiveInt(skip) { "Skip value cannot be negative" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            db.boards.getBoardLists(it, boardId, skip, limit)
        }
    }

    fun getBoardUsers(token: String, boardId: Int, skip: Int? = null, limit: Int? = null): kotlin.collections.List<User> {
        validateId(boardId) { "Invalid board id" }
        validateToken(token) { "Invalid user token" }
        validatePositiveInt(limit) { "Limit value cannot be negative" }
        validatePositiveInt(skip) { "Skip value cannot be negative" }
        return db.fetch {
            val userId = it.authenticateUser(token)
            db.boards.checkIfUserInBoard(it, userId, boardId)
            val userIds = db.boards.getBoardUserIds(it, boardId, skip, limit)
            userIds.map { uid -> db.users.getUserById(it, uid) }
        }
    }

    fun addUserToBoard(token: String, userId: Int, boardId: Int) {
        validateToken(token) { "Invalid user token" }
        validateId(userId) { "Invalid user id" }
        validateId(boardId) { "Invalid board id" }
        return db.fetch {
            val id = it.authenticateUser(token)
            db.boards.checkIfUserInBoard(it, id, boardId)
            db.boards.addUserToBoard(it, userId, boardId)
        }
    }

    fun removeUserFromBoard(token: String, userId: Int, boardId: Int) {
        validateToken(token) { "Invalid user token" }
        validateId(userId) { "Invalid user id" }
        validateId(boardId) { "Invalid board id" }
        return db.fetch {
            val id = it.authenticateUser(token)
            db.boards.checkIfUserInBoard(it, id, boardId)
            db.boards.removeUserFromBoard(it, userId, boardId)
        }
    }

    fun updateBoard(token: String, boardId: Int, name: String?, description: String?) {
        validateToken(token) { "Invalid user token" }
        validateId(boardId) { "Invalid board id" }
        if (name != null) validateName(name) { "Invalid board name" }
        if (description != null) validateDescription(description) { "Invalid board description" }
        return db.fetch {
            val id = it.authenticateUser(token)
            db.boards.checkIfUserInBoard(it, id, boardId)
            db.boards.updateBoard(it, boardId, name, description)
        }
    }

    fun deleteBoard(token: String, boardId: Int) {
        validateToken(token) { "Invalid user token" }
        validateId(boardId) { "Invalid board id" }
        return db.fetch {
            val id = it.authenticateUser(token)
            db.boards.checkIfUserInBoard(it, id, boardId)
            db.boards.deleteBoard(it, boardId)
        }
    }
}
