package pt.isel.integration.api

import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PATCH
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test
import pt.isel.integration.IntegrationTest
import pt.isel.ls.boardio.app.api.utils.decodeAs
import pt.isel.ls.boardio.app.api.utils.json
import pt.isel.ls.boardio.app.api.utils.setToken
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.utils.SearchResponse
import pt.isel.ls.boardio.domain.boards.api.CreateBoardResponse
import pt.isel.ls.boardio.domain.cards.Card
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListsApiTest : IntegrationTest() {
    @Test
    fun `Create new list with valid parameters`() {
        val board = createTestBoard()
        val name = "list-${randomId()}"
        val request = Request(POST, "$baseURL/lists").json(
            "name" to name,
            "boardId" to board.id
        ).setToken(token)
        val response = client(request)
        val list = response.decodeAs<CreateBoardResponse>()
        assertEquals(Status.CREATED, response.status)
        assertTrue(validateId(list.id))
    }

    @Test
    fun `Create new list with invalid parameters`() {
        val request = Request(POST, "$baseURL/lists").json(
            "name" to "list name",
            "boardId" to "not a valid integer"
        ).setToken(token)
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `Get list with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val request = Request(GET, "$baseURL/lists/${list.id}").setToken(token)
        val response = client(request)
        val getListResponse = response.decodeAs<pt.isel.ls.boardio.domain.lists.List>()
        assertEquals(Status.OK, response.status)
        assertEquals(list.name, getListResponse.name)
    }

    @Test
    fun `Get list with invalid id`() {
        val request = Request(GET, "$baseURL/lists/abc").setToken(token)
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `Get non existing list`() {
        val request = Request(GET, "$baseURL/lists/99").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `Get list cards with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val request = Request(GET, "$baseURL/lists/${list.id}/cards").setToken(token)
        val response = client(request)
        val listCards = response.decodeAs<List<Card>>()
        assertEquals(Status.OK, response.status)
        assertEquals(emptyList(), listCards)
    }

    @Test
    fun `Get list cards with invalid list id`() {
        val request = Request(GET, "$baseURL/lists/99/cards").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `delete a created list with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val deleteListRequest = Request(DELETE, "$baseURL/lists/${list.id}").setToken(token)
        val deleteListResponse = client(deleteListRequest)
        assertEquals(Status.OK, deleteListResponse.status)

        val getListRequest = Request(GET, "$baseURL/lists/${list.id}").setToken(token)
        val getListResponse = client(getListRequest)
        assertEquals(Status.NOT_FOUND, getListResponse.status)
    }

    @Test
    fun `delete a created list with invalid id`() {
        val request = Request(DELETE, "$baseURL/lists/abc").setToken(token)
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `delete a non existing list`() {
        val request = Request(DELETE, "$baseURL/lists/99").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `move a list with valid parameters`() {
        val board = createTestBoard()
        val list1 = createTestList(board.id)
        val list2 = createTestList(board.id)

        val moveListRequest = Request(PATCH, "$baseURL/lists/${list1.id}/move?index=${list2.index}").setToken(token)
        val moveListResponse = client(moveListRequest)
        assertEquals(Status.OK, moveListResponse.status)

        val getList1 = getList(list1.id).result
        val getList2 = getList(list2.id).result
        assertEquals(list2.index, getList1.index)
        assertEquals(list1.index, getList2.index)
    }

    @Test
    fun `move a list with invalid list id`() {
        val request = Request(PATCH, "$baseURL/lists/0/move?index=0").setToken(token)
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `archive a list with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)

        val archiveListRequest = Request(PATCH, "$baseURL/lists/${list.id}").json(
            "archived" to true
        ).setToken(token)
        val archiveListResponse = client(archiveListRequest)
        assertEquals(Status.OK, archiveListResponse.status)

        val response = getList(list.id)
        assertEquals(Status.OK, response.status)
        assertTrue(response.result.archived)
    }

    @Test
    fun `update a list with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)

        val newListName = "list name updated"
        val request = Request(PATCH, "$baseURL/lists/${list.id}").json(
            "name" to newListName
        ).setToken(token)
        val response = client(request)
        assertEquals(Status.OK, response.status)

        val listResponse = getList(list.id)
        assertEquals(Status.OK, listResponse.status)
        assertEquals(newListName, listResponse.result.name)
    }

    @Test
    fun `search for a list`() {
        val board = createTestBoard()
        val list = createTestList(board.id)

        val request = Request(GET, "$baseURL/search?query=${list.name}&type=lists").setToken(token)
        val response = client(request)
        assertEquals(Status.OK, response.status)

        val (results, total) = response.decodeAs<SearchResponse>()
        assertEquals(Status.OK, response.status)
        assertEquals(list.name, results.first().name)
        assertEquals(1, total)
    }
}
