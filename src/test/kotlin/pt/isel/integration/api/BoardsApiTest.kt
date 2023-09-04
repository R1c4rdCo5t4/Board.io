package pt.isel.integration.api

import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PATCH
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test
import pt.isel.integration.IntegrationTest
import pt.isel.ls.boardio.app.api.utils.decodeAs
import pt.isel.ls.boardio.app.api.utils.json
import pt.isel.ls.boardio.app.api.utils.setToken
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.utils.SearchResponse
import pt.isel.ls.boardio.domain.boards.Board
import pt.isel.ls.boardio.domain.boards.api.CreateBoardResponse
import pt.isel.ls.boardio.domain.boards.api.GetBoardUserResponse
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoardsApiTest : IntegrationTest() {

    @Test
    fun `Create new board with valid parameters`() {
        val name = "board-${randomId()}"
        val desc = "board-description"
        val request = Request(POST, "$baseURL/boards").json(
            "name" to name,
            "description" to desc
        ).setToken(token)
        val response = client(request)
        val board = response.decodeAs<CreateBoardResponse>()
        assertEquals(Status.CREATED, response.status)
        assertTrue(validateId(board.id))
    }

    @Test
    fun `Create new board with invalid JSON`() {
        val request = Request(POST, "$baseURL/boards").json(
            "name" to "Board ${randomId()}",
            "desc" to "Invalid field"
        ).setToken(token)
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `Create new board with invalid description`() {
        val request = Request(POST, "$baseURL/boards").json(
            "name" to "board",
            "description" to ""
        ).setToken(token)
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `Should fail create a board with a name that already exists`() {
        val board = createTestBoard()
        val request = Request(POST, "$baseURL/boards").json(
            "name" to board.name,
            "description" to "Board Description"
        ).setToken(token)
        val response = client(request)
        assertEquals(Status.CONFLICT, response.status)
    }

    @Test
    fun `Get board with valid id`() {
        val board = createTestBoard()
        val getBoardRequest = Request(GET, "$baseURL/boards/${board.id}").setToken(token)
        val response = client(getBoardRequest)
        val getBoardResponse = response.decodeAs<Board>()
        assertEquals(Status.OK, response.status)
        assertEquals(board.name, getBoardResponse.name)
        assertEquals(board.description, getBoardResponse.description)
    }

    @Test
    fun `Get board with invalid id`() {
        val getBoardRequest = Request(GET, "$baseURL/boards/abc").setToken(token)
        val getBoardResponse = client(getBoardRequest)
        assertEquals(Status.BAD_REQUEST, getBoardResponse.status)
    }

    @Test
    fun `Get non existing board`() {
        val getBoardRequest = Request(GET, "$baseURL/boards/99").setToken(token)
        val getBoardResponse = client(getBoardRequest)
        assertEquals(Status.NOT_FOUND, getBoardResponse.status)
    }

    @Test
    fun `Get board lists with valid id`() {
        val board = createTestBoard()
        val request = Request(GET, "$baseURL/boards/${board.id}/lists").setToken(token)
        val response = client(request)
        val lists = response.decodeAs<List<pt.isel.ls.boardio.domain.lists.List>>()
        assertEquals(Status.OK, response.status)
        assertEquals(emptyList(), lists)
    }

    @Test
    fun `Get lists from non existing board`() {
        val request = Request(GET, "$baseURL/boards/99/lists").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `Get users from existing board`() {
        val board = createTestBoard()
        val request = Request(GET, "$baseURL/boards/${board.id}/users").setToken(token)
        val response = client(request)
        val users = response.decodeAs<List<GetBoardUserResponse>>()
        assertEquals(Status.OK, response.status)
        assertEquals(1, users.size)
    }

    @Test
    fun `Get users from non existing board`() {
        val request = Request(GET, "$baseURL/boards/99/users").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `Get user boards with valid id`() {
        val user = createTestUser()
        val request = Request(GET, "$baseURL/boards").setToken(user.token)
        val response = client(request)
        val boards = response.decodeAs<List<Board>>()
        assertEquals(Status.OK, response.status)
        assertEquals(emptyList(), boards)
    }

    @Test
    fun `Get user boards from non existing user`() {
        val randomToken = UUID.randomUUID().toString()
        val request = Request(GET, "$baseURL/boards").setToken(randomToken)
        val response = client(request)
        assertEquals(Status.UNAUTHORIZED, response.status)
    }

    @Test
    fun `Update board with valid id`() {
        val board = createTestBoard()
        val newBoardName = "board name updated"
        val newBoardDescription = "board description updated"
        val updateBoardRequest = Request(PATCH, "$baseURL/boards/${board.id}").json(
            "name" to newBoardName,
            "description" to newBoardDescription
        ).setToken(token)
        val updateBoardResponse = client(updateBoardRequest)
        assertEquals(Status.OK, updateBoardResponse.status)

        val getBoardRequest = Request(GET, "$baseURL/boards/${board.id}").setToken(token)
        val getBoardResponse = client(getBoardRequest)
        val gotBoard = getBoardResponse.decodeAs<Board>()
        assertEquals(newBoardName, gotBoard.name)
        assertEquals(newBoardDescription, gotBoard.description)
    }

    @Test
    fun `Delete board with valid id`() {
        val board = createTestBoard()
        val deleteBoardRequest = Request(DELETE, "$baseURL/boards/${board.id}").setToken(token)
        val deleteBoardResponse = client(deleteBoardRequest)
        assertEquals(Status.OK, deleteBoardResponse.status)

        val getBoardRequest = Request(GET, "$baseURL/boards/${board.id}").setToken(token)
        val getBoardResponse = client(getBoardRequest)
        assertEquals(Status.NOT_FOUND, getBoardResponse.status)
    }

    @Test
    fun `Add user to board by ids`() {
        val user = createTestUser()
        val board = createTestBoard()
        val addUserToBoardRequest = Request(PUT, "$baseURL/boards/${board.id}/add?userId=${user.id}").setToken(token)
        val addUserToBoardResponse = client(addUserToBoardRequest)
        assertEquals(Status.OK, addUserToBoardResponse.status)

        val request = Request(GET, "$baseURL/boards").setToken(user.token)
        val response = client(request)
        val boards = response.decodeAs<List<Board>>()
        assertEquals(Status.OK, response.status)
        assertEquals(1, boards.size)
    }

    @Test
    fun `Add user to board by invalid ids`() {
        val request = Request(PUT, "$baseURL/boards/99/add?userId=99").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `Remove user from board by ids`() {
        val user = createTestUser()
        val board = createTestBoard()

        val addUserToBoardRequest = Request(PUT, "$baseURL/boards/${board.id}/add?userId=${user.id}").setToken(token)
        val addUserToBoardResponse = client(addUserToBoardRequest)
        assertEquals(Status.OK, addUserToBoardResponse.status)

        val removeUserFromBoardRequest = Request(PUT, "$baseURL/boards/${board.id}/remove?userId=${user.id}").setToken(token)
        val removeUserFromBoardResponse = client(removeUserFromBoardRequest)
        assertEquals(Status.OK, removeUserFromBoardResponse.status)

        val request = Request(GET, "$baseURL/boards").setToken(user.token)
        val response = client(request)
        val boards = response.decodeAs<List<Board>>()
        assertEquals(Status.OK, response.status)
        assertEquals(0, boards.size)
    }

    @Test
    fun `search for a board`() {
        val board = createTestBoard()
        val request = Request(GET, "$baseURL/search?query=${board.name}&type=boards")
            .setToken(token)
        val response = client(request)
        val (results, total) = response.decodeAs<SearchResponse>()

        assertEquals(Status.OK, response.status)
        assertEquals(board.name, results.first().name)
        assertEquals(1, total)
    }
}
