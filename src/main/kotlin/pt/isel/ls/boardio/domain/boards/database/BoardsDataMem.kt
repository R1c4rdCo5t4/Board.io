package pt.isel.ls.boardio.domain.boards.database

import pt.isel.ls.boardio.app.database.datamem.checkIfAlreadyExists
import pt.isel.ls.boardio.app.database.datamem.getNextId
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.ForbiddenException
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.app.utils.subSequence
import pt.isel.ls.boardio.domain.boards.Board
import pt.isel.ls.boardio.domain.boards.UserBoard
import pt.isel.ls.boardio.domain.lists.List

class BoardsDataMem : BoardsSource {
    override fun createBoard(source: Source, name: String, description: String): Int {
        checkIfAlreadyExists(source.mem.boards, name, Board::name)
        val boardId = source.mem.boards.keys.getNextId()
        source.mem.boards[boardId] = Board(boardId, name, description)
        return boardId
    }

    override fun getBoard(source: Source, boardId: Int): Board {
        return source.mem.boards[boardId] ?: throw NotFoundException("Board with id $boardId was not found")
    }

    override fun getUserBoards(source: Source, userId: Int, skip: Int?, limit: Int?): kotlin.collections.List<Board> {
        if (!source.mem.users.containsKey(userId)) throw NotFoundException("User with id $userId not found")
        val boardIds = source.mem.userBoards.mapNotNull { if (it.userId == userId) it.boardId else null }
        val boards = boardIds.mapNotNull { source.mem.boards[it] }
        return boards.subSequence(skip, limit)
    }

    override fun getBoardLists(source: Source, boardId: Int, skip: Int?, limit: Int?): kotlin.collections.List<List> {
        if (!source.mem.boards.containsKey(boardId)) throw NotFoundException("Board with id $boardId was not found")
        val lists = source.mem.lists.values.filter { it.boardId == boardId }
        return lists.subSequence(skip, limit)
    }

    override fun checkIfUserInBoard(source: Source, userId: Int, boardId: Int) {
        if (!source.mem.boards.containsKey(boardId)) throw NotFoundException("Board with id $boardId was not found")
        val userIdsInBoard = source.mem.userBoards.mapNotNull { if (it.boardId == boardId) it.userId else null }
        if (userId !in userIdsInBoard) {
            throw ForbiddenException("User not in board")
        }
    }

    override fun getBoardUserIds(source: Source, boardId: Int, skip: Int?, limit: Int?): kotlin.collections.List<Int> {
        if (!source.mem.boards.containsKey(boardId)) throw NotFoundException("Board with id $boardId was not found")
        return source.mem.userBoards
            .filter { it.boardId == boardId }
            .mapNotNull { it.userId }
            .subSequence(skip, limit)
    }

    override fun addUserToBoard(source: Source, userId: Int, boardId: Int) {
        source.mem.userBoards.add(UserBoard(userId, boardId))
    }

    override fun removeUserFromBoard(source: Source, userId: Int, boardId: Int) {
        source.mem.userBoards.removeAll { it.userId == userId && it.boardId == boardId }
    }

    override fun updateBoard(source: Source, boardId: Int, name: String?, description: String?) {
        val board = source.mem.boards[boardId] ?: throw NotFoundException("Board with id $boardId was not found")
        if (name != null) checkIfAlreadyExists(source.mem.boards, name, Board::name)
        source.mem.boards[boardId] = board.copy(name = name ?: board.name, description = description ?: board.description)
    }

    override fun deleteBoard(source: Source, boardId: Int) {
        source.mem.boards.remove(boardId)
        source.mem.userBoards.removeAll { it.boardId == boardId }
        val lists = source.mem.lists.values.filter { it.boardId == boardId }
        source.mem.lists.values.removeAll { it.boardId == boardId }
        lists.forEach { list ->
            source.mem.cards.values.removeAll { it.listId == list.id }
        }
    }
}
