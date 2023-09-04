package pt.isel.ls.boardio.domain.boards.database

import pt.isel.ls.boardio.app.database.database.checkIfAlreadyExists
import pt.isel.ls.boardio.app.database.database.checkIfNotExists
import pt.isel.ls.boardio.app.database.database.execute
import pt.isel.ls.boardio.app.database.database.executeQueries
import pt.isel.ls.boardio.app.database.database.executeQuery
import pt.isel.ls.boardio.app.database.database.getList
import pt.isel.ls.boardio.app.database.database.getValuesToUpdate
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.ForbiddenException
import pt.isel.ls.boardio.app.utils.subSequence
import pt.isel.ls.boardio.app.utils.toLocalDateTime
import pt.isel.ls.boardio.domain.boards.Board
import pt.isel.ls.boardio.domain.lists.List

class BoardsDatabase : BoardsSource {

    override fun createBoard(source: Source, name: String, description: String): Int {
        source.conn.checkIfAlreadyExists("board", "name", name) { "Board with name $name already exists" }
        val stm = "insert into board(name, description) values (?,?) returning id"
        val args = listOf(name, description)
        val columns = listOf("id")
        val (boardId) = source.conn.executeQuery(stm, args, columns)
        return boardId.toInt()
    }

    override fun getBoard(source: Source, boardId: Int): Board {
        val stm = "select name, description, createdDate from board where id = ?"
        val args = listOf(boardId)
        val (name, description, createdDate) = source.conn.executeQuery(stm, args) { "Board with id $boardId not found" }
        return Board(boardId, name, description, createdDate.toLocalDateTime())
    }

    override fun getUserBoards(source: Source, userId: Int, skip: Int?, limit: Int?): kotlin.collections.List<Board> {
        source.conn.checkIfNotExists("\"user\"", "id", userId) { "User with id $userId not found" }
        val stm = "select id, name, description, createdDate " +
            "from userBoard join board on boardId = id " +
            "where userId = ?"

        val args = listOf(userId)
        val results = source.conn.executeQueries(stm, args)
        return results.map { board ->
            val (id, name, description, createdDate) = board
            Board(id.toInt(), name, description, createdDate.toLocalDateTime())
        }.subSequence(skip, limit)
    }

    override fun getBoardLists(source: Source, boardId: Int, skip: Int?, limit: Int?): kotlin.collections.List<List> {
        source.conn.checkIfNotExists("board", "id", boardId) { "Board with id $boardId not found" }
        val stm = "select id, name, boardId, index, archived, createdDate from list where boardId = ? order by index"
        val args = listOf(boardId)
        val queries = source.conn.executeQueries(stm, args)
        return queries.map { source.conn.getList(it) }.subSequence(skip, limit)
    }

    override fun checkIfUserInBoard(source: Source, userId: Int, boardId: Int) {
        source.conn.checkIfNotExists("\"user\"", "id", userId) { "User with id $userId not found" }
        source.conn.checkIfNotExists("board", "id", boardId) { "Board with id $boardId not found" }
        val userIdsInBoard = getBoardUserIds(source, boardId, null, null)
        if (userId !in userIdsInBoard) {
            throw ForbiddenException("User not in board")
        }
    }

    override fun getBoardUserIds(source: Source, boardId: Int, skip: Int?, limit: Int?): kotlin.collections.List<Int> {
        val stm = "select userId from userboard where boardId = ?"
        val args = listOf(boardId)
        val results = source.conn.executeQueries(stm, args)
        return results.map { it.first() }.map { it.toInt() }.subSequence(skip, limit)
    }

    override fun addUserToBoard(source: Source, userId: Int, boardId: Int) {
        source.conn.checkIfAlreadyExists("userBoard", "userId", userId, "boardId", boardId) { "User already in board" }
        source.conn.checkIfNotExists("\"user\"", "id", userId) { "User with id $userId not found" }
        source.conn.checkIfNotExists("board", "id", boardId) { "Board with id $boardId not found" }
        val stm = "insert into userBoard(userId, boardId) values (?,?)"
        val args = listOf(userId, boardId)
        source.conn.execute(stm, args)
    }

    override fun removeUserFromBoard(source: Source, userId: Int, boardId: Int) {
        source.conn.checkIfNotExists("\"user\"", "id", userId) { "User with id $userId not found" }
        source.conn.checkIfNotExists("board", "id", boardId) { "Board with id $boardId not found" }
        val stm = "delete from userBoard where userId = ? and boardId = ?"
        val args = listOf(userId, boardId)
        source.conn.execute(stm, args)
    }

    override fun updateBoard(source: Source, boardId: Int, name: String?, description: String?) {
        source.conn.checkIfNotExists("board", "id", boardId) { "Board with id $boardId not found" }
        source.conn.checkIfAlreadyExists("board", "name", name) { "Board with name $name already exists" }
        val (columns, valuesToUpdate) = getValuesToUpdate(
            "name" to name,
            "description" to description
        )
        val stm = "update board set $columns where id = ?"
        val args = listOf(*valuesToUpdate, boardId)
        source.conn.execute(stm, args)
    }

    override fun deleteBoard(source: Source, boardId: Int) {
        source.conn.checkIfNotExists("board", "id", boardId) { "Board with id $boardId not found" }
        val stm = "delete from board where id = ?"
        val arg = listOf(boardId)
        source.conn.execute(stm, arg)
    }
}
