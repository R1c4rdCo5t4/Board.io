package pt.isel.unit.datamem

import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.ls.boardio.app.utils.toLocalDateTime
import pt.isel.unit.UnitTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CardsDataMemTest : UnitTest() {

    @Test
    fun `createCard should create card in list and return its id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val name = "Test card"
        val description = "This is a test card"
        val card = mem.fetch {
            val id = mem.cards.createCard(it, name, description, list.id)
            mem.cards.getCard(it, id)
        }
        assertEquals(name, card.name)
        assertEquals(description, card.description)
        assertEquals(list.id, card.listId)
    }

    @Test
    fun `getCard should get the details of a specific card by id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val testCard = createTestCard(list.id)
        val card = mem.fetch {
            mem.cards.getCard(it, testCard.id)
        }
        assertEquals(testCard.id, card.id)
        assertEquals(testCard.name, card.name)
        assertEquals(testCard.description, card.description)
        assertEquals(testCard.listId, card.listId)
    }

    @Test
    fun `moveCard should move card from a list to another`() {
        val board = createTestBoard()
        val list1 = createTestList(board.id)
        val list2 = createTestList(board.id)
        val card = createTestCard(list1.id)
        val index = 0

        mem.fetch { mem.cards.moveCard(it, card.id, list2.id, index) }
        val movedCard = mem.fetch { mem.cards.getCard(it, card.id) }
        assertEquals(list2.id, movedCard.listId)
        assertEquals(index, movedCard.index)
    }

    @Test
    fun `moveCard with invalid list id should throw NotFoundException`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        assertFailsWith<NotFoundException> {
            mem.fetch { mem.cards.moveCard(it, card.id, 999, 1) }
        }
    }

    @Test
    fun `delete a card with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        assertNotNull(card)
        mem.fetch { mem.cards.deleteCard(it, card.id) }
        assertFailsWith<NotFoundException> {
            mem.fetch { mem.cards.getCard(it, card.id) }
        }
    }

    @Test
    fun `delete a card with invalid id`() {
        assertFailsWith<NotFoundException> {
            mem.fetch { mem.cards.deleteCard(it, 99) }
        }
    }

    @Test
    fun `archive a card with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        mem.fetch { mem.cards.updateCard(it, card.id, null, null, true) }
        val updatedCard = mem.fetch { mem.cards.getCard(it, card.id) }
        assertTrue(updatedCard.archived)
    }

    @Test
    fun `update a card with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        val newName = "New name"
        val newDescription = "New description"
        mem.fetch { mem.cards.updateCard(it, card.id, newName, newDescription, null) }
        val updatedCard = mem.fetch { mem.cards.getCard(it, card.id) }
        assertEquals(newName, updatedCard.name)
        assertEquals(newDescription, updatedCard.description)
    }

    @Test
    fun `update due date in a valid card`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        val newDueDate = "2030-12-12T12:12:12".toLocalDateTime()
        mem.fetch { mem.cards.updateCardDueDate(it, card.id, newDueDate) }
        val updatedCard = mem.fetch { mem.cards.getCard(it, card.id) }
        assertEquals(newDueDate, updatedCard.dueDate)
    }

    @Test
    fun `search a card`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card = createTestCard(list.id)
        val (results, total) = mem.search(token, SearchQuery("card", listOf("cards")))
        assertEquals(1, total)
        assertEquals(card.id, results.first().id)
        assertEquals(card.name, results.first().name)
    }
}
