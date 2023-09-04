package pt.isel.ls.boardio.domain.boards

import pt.isel.ls.boardio.app.services.utils.validateId

data class UserBoard(
    val userId: Int,
    val boardId: Int
) {
    init {
        validateId(userId) { "Invalid user id" }
        validateId(boardId) { "Invalid board id" }
    }
}
