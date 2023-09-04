package pt.isel.unit.services

import junit.framework.TestCase.assertEquals
import org.junit.Test
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.app.utils.toLocalDateTime
import pt.isel.unit.UnitTest
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CardsUnitTest : UnitTest() {

    @Test
    fun `createCard should create a card with a unique id and return it`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Description 1")
        val listId = services.lists.createList(token, "Board 1", boardId)
        val id = services.cards.createCard(token, "Card 1", "Description 1", listId)
        assertEquals(1, id)
    }

    @Test
    fun `createCard should throw IllegalArgumentException if invalid parameters provided`() {
        val validName = "Card 1"
        val validDescription = "Description 1"
        assertFailsWith<IllegalArgumentException> { services.cards.createCard(token, "", validDescription, 1) }
        assertFailsWith<IllegalArgumentException> { services.cards.createCard("", validName, validDescription, 1) }
        assertFailsWith<IllegalArgumentException> { services.cards.createCard(token, validName, validDescription, 0) }
    }

    @Test
    fun `getCard should return the card by id`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Description 1")
        val listId = services.lists.createList(token, "Board 1", boardId)
        val name = "Card 1"
        val description = "Description 1"
        val id = services.cards.createCard(token, name, description, listId)
        val card = services.cards.getCard(token, id)
        assertEquals(id, card.id)
        assertEquals(name, card.name)
        assertEquals(description, card.description)
        assertEquals(listId, card.listId)
    }

    @Test
    fun `getCard should throw IllegalArgumentException if invalid parameters provided`() {
        assertFailsWith<IllegalArgumentException> { services.cards.getCard(token, 0) }
        assertFailsWith<IllegalArgumentException> { services.cards.getCard("abc", 0) }
    }

    @Test
    fun `moveCard should move the card for the specified list by ids`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Board 1 description")
        val listIdSrc = services.lists.createList(token, "List 1", boardId)
        val listIdDest = services.lists.createList(token, "List 2", boardId)
        val card = services.cards.createCard(token, "Card 1", "Description 1", listIdSrc)
        assertEquals(listIdSrc, services.cards.getCard(token, card).listId)
        services.cards.moveCard(token, card, listIdDest, 1)
        assertEquals(listIdDest, services.cards.getCard(token, card).listId)
    }

    @Test
    fun `moveCard should throw IllegalArgumentException if invalid parameters provided`() {
        assertFailsWith<IllegalArgumentException> { services.cards.moveCard("abc", 2, 0, 1) }
        assertFailsWith<IllegalArgumentException> { services.cards.moveCard(token, 2, 0, -1) }
    }

    @Test
    fun `delete a created card with valid id`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Board 1 description")
        val listId = services.lists.createList(token, "List 1", boardId)
        val card = services.cards.createCard(token, "Card 1", "Description 1", listId)
        val createdCard = services.cards.getCard(token, card)
        assertNotNull(createdCard)
        assertEquals(listId, createdCard.listId)
        services.cards.deleteCard(token, card)
        assertFailsWith<NotFoundException> { services.cards.getCard(token, card) }
    }

    @Test
    fun `delete a card with invalid id`() {
        assertFailsWith<IllegalArgumentException> { services.cards.deleteCard(token, 0) }
        assertFailsWith<IllegalArgumentException> { services.cards.deleteCard("abc", 1) }
    }

    @Test
    fun `archive a created card with valid id`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Board 1 description")
        val listId = services.lists.createList(token, "List 1", boardId)
        val createdCard = services.cards.createCard(token, "Card 1", "Description 1", listId)
        val card = services.cards.getCard(token, createdCard)
        assertNotNull(createdCard)
        assertEquals(listId, card.listId)
        services.cards.updateCard(token, card.id, null, null, true)
        assertEquals(true, services.cards.getCard(token, card.id).archived)
    }

    @Test
    fun `update a created card with valid id`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Board 1 description")
        val listId = services.lists.createList(token, "List 1", boardId)
        val createdCard = services.cards.createCard(token, "Card 1", "Description 1", listId)
        val card = services.cards.getCard(token, createdCard)
        assertNotNull(createdCard)
        assertEquals(listId, card.listId)
        services.cards.updateCard(token, card.id, "Card 2", "Description 2", null)
        val updatedCard = services.cards.getCard(token, card.id)
        assertEquals("Card 2", updatedCard.name)
        assertEquals("Description 2", updatedCard.description)
    }

    @Test
    fun `update a card with invalid id`() {
        assertFailsWith<IllegalArgumentException> { services.cards.updateCard(token, 0, "Card 2", "Description 2", null) }
        assertFailsWith<IllegalArgumentException> { services.cards.updateCard("abc", 1, "Card 2", "Description 2", null) }
    }

    @Test
    fun `update due date in a valid card`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Board 1 description")
        val listId = services.lists.createList(token, "List 1", boardId)
        val createdCard = services.cards.createCard(token, "Card 1", "Description 1", listId)
        val card = services.cards.getCard(token, createdCard)
        assertNotNull(createdCard)
        assertEquals(listId, card.listId)
        services.cards.updateCardDueDate(token, card.id, "2030-01-01T00:00:00".toLocalDateTime())
        val updatedCard = services.cards.getCard(token, card.id)
        assertEquals("2030-01-01T00:00:00".toLocalDateTime(), updatedCard.dueDate)
    }

    @Test
    fun `update due date in a invalid card`() {
        assertFailsWith<IllegalArgumentException> { services.cards.updateCardDueDate(token, 0, "2030-01-01T00:00:00".toLocalDateTime()) }
        assertFailsWith<IllegalArgumentException> { services.cards.updateCardDueDate("abc", 1, "2030-01-01T00:00:00".toLocalDateTime()) }
        assertFailsWith<IllegalArgumentException> { services.cards.updateCardDueDate(token, 1, "2020-01-01T00:00:00".toLocalDateTime()) }
    }
}
