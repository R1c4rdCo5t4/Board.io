package pt.isel.unit.datamem

import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.unit.UnitTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ListsDataMemTest : UnitTest() {

    @Test
    fun `createList should create a list in a board and return its id`() {
        val board = createTestBoard()
        val listName = "List"
        val list = mem.fetch {
            val listId = mem.lists.createList(it, listName, board.id)
            mem.lists.getList(it, listId)
        }
        assertEquals(listName, list.name)
        assertEquals(board.id, list.boardId)
        assertEquals(0, list.index)
    }

    @Test
    fun `getList should get the details of list by id`() {
        val testBoard = createTestBoard()
        val testList = createTestList(testBoard.id)
        val list = mem.fetch { mem.lists.getList(it, testList.id) }
        assertNotNull(list)
        assertEquals(testList.name, list.name)
        assertEquals(testBoard.id, list.boardId)
    }

    @Test
    fun `getListCards should get all the cards in a list`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val card1 = createTestCard(list.id)
        val card2 = createTestCard(list.id)
        val card3 = createTestCard(list.id)

        val cards = mem.fetch { mem.lists.getListCards(it, list.id) }
        assertEquals(3, cards.size)
        assertEquals(listOf(card1, card2, card3), cards)
    }

    @Test
    fun `delete a created list with valid id`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val lists = mem.fetch {
            mem.lists.deleteList(it, list.id)
            mem.boards.getBoardLists(it, board.id)
        }
        assertEquals(0, lists.size)
    }

    @Test
    fun `move a list to a new index`() {
        val board = createTestBoard()
        val list1 = createTestList(board.id)
        val list2 = createTestList(board.id)
        assertEquals(0, list1.index)
        assertEquals(1, list2.index)
        mem.fetch { mem.lists.moveList(it, list1.id, 1) }
        val movedList1 = mem.fetch { mem.lists.getList(it, list1.id) }
        val movedList2 = mem.fetch { mem.lists.getList(it, list2.id) }
        assertEquals(1, movedList1.index)
        assertEquals(0, movedList2.index)
    }

    @Test
    fun `archive a list`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        assertFalse(list.archived)
        mem.fetch { mem.lists.updateList(it, list.id, null, null, true) }
        val archivedList = mem.fetch { mem.lists.getList(it, list.id) }
        assertTrue(archivedList.archived)
    }

    @Test
    fun `update a list name`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val newName = "New List Name"
        mem.fetch { mem.lists.updateList(it, list.id, newName, null, null) }
        val updatedList = mem.fetch { mem.lists.getList(it, list.id) }
        assertEquals(newName, updatedList.name)
    }

    @Test
    fun `search a list`() {
        val board = createTestBoard()
        val list = createTestList(board.id)
        val (results, total) = mem.search(token, SearchQuery("list", listOf("lists")))
        assertEquals(1, total)
        assertEquals(list.id, results.first().id)
        assertEquals(list.name, results.first().name)
    }
}
