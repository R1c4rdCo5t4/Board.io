package pt.isel.integration

import org.http4k.client.JavaHttpClient
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.junit.AfterClass
import org.junit.BeforeClass
import pt.isel.ls.boardio.app.AppServer
import pt.isel.ls.boardio.app.api.utils.decodeAs
import pt.isel.ls.boardio.app.api.utils.json
import pt.isel.ls.boardio.app.api.utils.setToken
import pt.isel.ls.boardio.domain.boards.Board
import pt.isel.ls.boardio.domain.boards.api.CreateBoardResponse
import pt.isel.ls.boardio.domain.cards.Card
import pt.isel.ls.boardio.domain.users.User
import pt.isel.ls.boardio.domain.users.api.CreateUserResponse
import java.util.UUID

abstract class IntegrationTest {
    private val testUser = createTestUser()
    val id = testUser.id
    val token = testUser.token

    companion object {
        private const val port = 9001
        private val server = AppServer(port)
        const val baseURL = "http://localhost:$port/api"
        val client = JavaHttpClient()

        @BeforeClass
        @JvmStatic
        fun setup() {
            server.start()
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            server.stop()
        }

        fun randomId() = UUID.randomUUID().toString().substring(0, 6)
    }

    class ApiResponse<T>(private val response: Response, val result: T) {
        val status get() = response.status
    }

    fun createTestUser(): User {
        val username = "test-user-${randomId()}"
        val email = "test${randomId()}@test.kt"
        val password = "12345678"
        val request = Request(POST, "$baseURL/users").json(
            "name" to username,
            "email" to email,
            "password" to password
        )
        val response = client(request)
        val (id, token) = response.decodeAs<CreateUserResponse>()
        return User(id, username, email, token, password)
    }

    fun createTestBoard(): Board {
        val name = "board-${randomId()}"
        val desc = "board-description"
        val request = Request(POST, "$baseURL/boards").json(
            "name" to name,
            "description" to desc
        ).setToken(token)
        val response = client(request)
        val board = response.decodeAs<CreateBoardResponse>()
        return Board(board.id, name, desc)
    }

    fun createTestList(boardId: Int): pt.isel.ls.boardio.domain.lists.List {
        val name = "list-${randomId()}"
        val request = Request(POST, "$baseURL/lists").json(
            "name" to name,
            "boardId" to boardId
        ).setToken(token)
        val response = client(request).decodeAs<CreateBoardResponse>()
        val list = getList(response.id).result
        return pt.isel.ls.boardio.domain.lists.List(list.id, list.name, list.boardId, list.index)
    }

    fun createTestCard(listId: Int): Card {
        val name = "card-${randomId()}"
        val description = "card-description"
        val request = Request(POST, "$baseURL/cards").json(
            "name" to name,
            "description" to description,
            "listId" to listId
        ).setToken(token)
        val response = client(request).decodeAs<CreateBoardResponse>()
        val card = getCard(response.id).result
        return Card(response.id, name, description, listId, card.index)
    }

    fun getList(listId: Int): ApiResponse<pt.isel.ls.boardio.domain.lists.List> {
        val request = Request(GET, "$baseURL/lists/$listId").setToken(token)
        val response = client(request)
        val list = response.decodeAs<pt.isel.ls.boardio.domain.lists.List>()
        return ApiResponse(response, list)
    }

    fun getCard(cardId: Int): ApiResponse<Card> {
        val request = Request(GET, "$baseURL/cards/$cardId").setToken(token)
        val response = client(request)
        val card = response.decodeAs<Card>()
        return ApiResponse(response, card)
    }
}
