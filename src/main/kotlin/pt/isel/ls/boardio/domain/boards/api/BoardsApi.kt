package pt.isel.ls.boardio.domain.boards.api

import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PATCH
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.boardio.app.api.AppApi.Companion.request
import pt.isel.ls.boardio.app.api.utils.decodeAs
import pt.isel.ls.boardio.app.api.utils.getIntParameter
import pt.isel.ls.boardio.app.api.utils.getIntQuery
import pt.isel.ls.boardio.app.api.utils.getOptionalIntQueries
import pt.isel.ls.boardio.app.api.utils.getToken
import pt.isel.ls.boardio.app.api.utils.json
import pt.isel.ls.boardio.app.api.utils.message
import pt.isel.ls.boardio.domain.boards.services.BoardsServices

class BoardsApi(private val services: BoardsServices) {

    val routes = routes(
        "/" bind GET to request { getUserBoards(it) },
        "/" bind POST to request { createBoard(it) },
        "/{boardId}" bind GET to request { getBoard(it) },
        "/{boardId}" bind PATCH to request { updateBoard(it) },
        "/{boardId}" bind DELETE to request { deleteBoard(it) },
        "/{boardId}/lists" bind GET to request { getBoardLists(it) },
        "/{boardId}/users" bind GET to request { getBoardUsers(it) },
        "/{boardId}/add" bind PUT to request { addUserToBoard(it) },
        "/{boardId}/remove" bind PUT to request { removeUserFromBoard(it) }
    )

    private fun createBoard(request: Request): Response {
        val token = request.getToken()
        val board = request.decodeAs<CreateBoardRequest>()
        val boardId = services.createBoard(token, board.name, board.description)
        val response = CreateBoardResponse(boardId)
        return Response(CREATED).json(response)
    }

    private fun getBoard(request: Request): Response {
        val token = request.getToken()
        val boardId = request.getIntParameter("boardId")
        val board = services.getBoard(token, boardId)
        return Response(OK).json(board)
    }

    private fun getUserBoards(request: Request): Response {
        val token = request.getToken()
        val (skip, limit) = request.getOptionalIntQueries("skip", "limit")
        val boards = services.getUserBoards(token, skip, limit)
        return Response(OK).json(boards)
    }

    private fun getBoardLists(request: Request): Response {
        val token = request.getToken()
        val boardId = request.getIntParameter("boardId")
        val (skip, limit) = request.getOptionalIntQueries("skip", "limit")
        val lists = services.getBoardLists(token, boardId, skip, limit)
        return Response(OK).json(lists)
    }

    private fun getBoardUsers(request: Request): Response {
        val token = request.getToken()
        val boardId = request.getIntParameter("boardId")
        val (skip, limit) = request.getOptionalIntQueries("skip", "limit")
        val users = services.getBoardUsers(token, boardId, skip, limit)
        val response = users.map { GetBoardUserResponse(it.name, it.email, it.id) }
        return Response(OK).json(response)
    }

    private fun addUserToBoard(request: Request): Response {
        val token = request.getToken()
        val boardId = request.getIntParameter("boardId")
        val userId = request.getIntQuery("userId")
        services.addUserToBoard(token, userId, boardId)
        return Response(OK).message("User added to board successfully")
    }

    private fun removeUserFromBoard(request: Request): Response {
        val token = request.getToken()
        val boardId = request.getIntParameter("boardId")
        val userId = request.getIntQuery("userId")
        services.removeUserFromBoard(token, userId, boardId)
        return Response(OK).message("User with id $userId removed from board")
    }

    private fun updateBoard(request: Request): Response {
        val token = request.getToken()
        val boardId = request.getIntParameter("boardId")
        val board = request.decodeAs<UpdateBoardRequest>()
        services.updateBoard(token, boardId, board.name, board.description)
        return Response(OK).message("Board updated successfully")
    }

    private fun deleteBoard(request: Request): Response {
        val token = request.getToken()
        val boardId = request.getIntParameter("boardId")
        services.deleteBoard(token, boardId)
        return Response(OK).message("Board deleted successfully")
    }
}
