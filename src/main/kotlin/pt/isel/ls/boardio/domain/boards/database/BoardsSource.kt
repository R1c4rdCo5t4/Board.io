package pt.isel.ls.boardio.domain.boards.database

import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.domain.boards.Board
import pt.isel.ls.boardio.domain.lists.List

interface BoardsSource {
    fun createBoard(source: Source, name: String, description: String): Int
    fun getBoard(source: Source, boardId: Int): Board
    fun getUserBoards(source: Source, userId: Int, skip: Int? = null, limit: Int? = null): kotlin.collections.List<Board>
    fun getBoardLists(source: Source, boardId: Int, skip: Int? = null, limit: Int? = null): kotlin.collections.List<List>
    fun checkIfUserInBoard(source: Source, userId: Int, boardId: Int)
    fun getBoardUserIds(source: Source, boardId: Int, skip: Int? = null, limit: Int? = null): kotlin.collections.List<Int>
    fun addUserToBoard(source: Source, userId: Int, boardId: Int)
    fun removeUserFromBoard(source: Source, userId: Int, boardId: Int)
    fun updateBoard(source: Source, boardId: Int, name: String?, description: String?)
    fun deleteBoard(source: Source, boardId: Int)
}
