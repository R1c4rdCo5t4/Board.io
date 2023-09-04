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
import pt.isel.ls.boardio.app.utils.toLocalDateTime
import pt.isel.ls.boardio.domain.boards.api.CreateBoardResponse
import pt.isel.ls.boardio.domain.cards.Card
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardsApiTest : IntegrationTest() {
    @Test
    fun `Create new card with valid parameters`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val name = "card-${randomId()}"
        val description = "card-description"
        val request = Request(POST, "$baseURL/cards").json(
            "name" to name,
            "description" to description,
            "listId" to list.id
        ).setToken(token)
        val response = client(request)
        val card = response.decodeAs<CreateBoardResponse>()
        assertEquals(Status.CREATED, response.status)
        assertTrue(validateId(card.id))
    }

    @Test
    fun `Create new card with invalid parameters`() {
        val request = Request(POST, "$baseURL/cards").json(
            "isel" to "leic",
            "ls" to "boardio"
        ).setToken(token)
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `Get card with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        val request = Request(GET, "$baseURL/cards/${card.id}").setToken(token)
        val response = client(request)
        val getCardResponse = response.decodeAs<Card>()
        assertEquals(Status.OK, response.status)
        assertEquals(card.name, getCardResponse.name)
        assertEquals(card.description, getCardResponse.description)
    }

    @Test
    fun `Get card with invalid id`() {
        val request = Request(GET, "$baseURL/cards/99").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `Move card with valid id to a valid list`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        val list2 = createTestList(board.id)

        val moveCardRequest = Request(PATCH, "$baseURL/cards/${card.id}/move?listId=${list2.id}&index=0").setToken(token)
        val moveCardResponse = client(moveCardRequest)
        assertEquals(Status.OK, moveCardResponse.status)

        val response = getCard(card.id)
        assertEquals(Status.OK, response.status)
        assertEquals(list2.id, response.result.listId)
    }

    @Test
    fun `Move card with valid id to a non existing list`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        val request = Request(PATCH, "$baseURL/cards/${card.id}/move?listId=99&index=1").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `delete a new card with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)

        val deleteCardRequest = Request(DELETE, "$baseURL/cards/${card.id}").setToken(token)
        val deleteCardResponse = client(deleteCardRequest)
        assertEquals(Status.OK, deleteCardResponse.status)

        val getCardRequest = Request(GET, "$baseURL/cards/${card.id}").setToken(token)
        val getCardResponse = client(getCardRequest)
        assertEquals(Status.NOT_FOUND, getCardResponse.status)
    }

    @Test
    fun `delete a card with non existing id`() {
        val request = Request(DELETE, "$baseURL/cards/99").setToken(token)
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `archive a card`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)

        val archiveCardRequest = Request(PATCH, "$baseURL/cards/${card.id}").json("archived" to true).setToken(token)
        val archiveCardResponse = client(archiveCardRequest)
        assertEquals(Status.OK, archiveCardResponse.status)

        val response = getCard(card.id)
        assertEquals(Status.OK, response.status)
        assertTrue(response.result.archived)
    }

    @Test
    fun `update a card`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)

        val newCardName = "card name updated"
        val newCardDescription = "card description updated"
        val updateCardRequest = Request(PATCH, "$baseURL/cards/${card.id}").setToken(token).json(
            "name" to newCardName,
            "description" to newCardDescription
        )
        val updateCardResponse = client(updateCardRequest)
        assertEquals(Status.OK, updateCardResponse.status)

        val response = getCard(card.id)
        assertEquals(Status.OK, response.status)
        assertEquals(newCardName, response.result.name)
        assertEquals(newCardDescription, response.result.description)
    }

    @Test
    fun `update card due date`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)

        val newCardDueDate = "2030-12-12T12:12:12"
        val updateCardRequest = Request(PATCH, "$baseURL/cards/${card.id}/duedate").setToken(token).json(
            "dueDate" to newCardDueDate
        )
        val updateCardResponse = client(updateCardRequest)
        assertEquals(Status.OK, updateCardResponse.status)

        val response = getCard(card.id)
        assertEquals(Status.OK, response.status)
        assertEquals(newCardDueDate.toLocalDateTime(), response.result.dueDate)
    }

    @Test
    fun `search for a card`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)

        val request = Request(GET, "$baseURL/search?query=${card.name}&type=cards").setToken(token)
        val response = client(request)
        assertEquals(Status.OK, response.status)

        val (results, total) = response.decodeAs<SearchResponse>()
        assertEquals(Status.OK, response.status)
        assertEquals(card.name, results.first().name)
        assertEquals(1, total)
    }
}
