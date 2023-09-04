package pt.isel.unit.services

import junit.framework.TestCase.assertEquals
import org.junit.Test
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.unit.UnitTest
import kotlin.test.assertFailsWith

class ListsUnitTest : UnitTest() {

    @Test
    fun `createList should create a list with a unique id and return it`() {
        services.boards.createBoard(token, "Board 1", "Description 1")
        val name = "List 1"
        val boardId = 1
        val id = services.lists.createList(token, name, boardId)
        assertEquals(1, id)
    }

    @Test
    fun `createList should throw IllegalArgumentException if invalid parameters provided`() {
        assertFailsWith<IllegalArgumentException> { services.lists.createList(token, "", 1) }
        assertFailsWith<IllegalArgumentException> { services.lists.createList("", "List 1", 1) }
        assertFailsWith<IllegalArgumentException> { services.lists.createList(token, "List 1", 0) }
    }

    @Test
    fun `getList should return the list by id`() {
        services.boards.createBoard(token, "Board 1", "Description 1")
        val name = "List 1"
        val boardId = 1
        val index = 0
        val id = services.lists.createList(token, name, boardId)
        val list = services.lists.getList(token, id)
        assertEquals(id, list.id)
        assertEquals(name, list.name)
        assertEquals(boardId, list.boardId)
        assertEquals(index, list.index)
    }

    @Test
    fun `getList should throw IllegalArgumentException if invalid parameters provided`() {
        assertFailsWith<IllegalArgumentException> { services.lists.getList(token, 0) }
        assertFailsWith<IllegalArgumentException> { services.lists.getList("abc", 0) }
    }

    @Test
    fun `getCardsFromList should throw NotFoundException because the list does not exist`() {
        assertFailsWith<NotFoundException> { services.lists.getListCards(token, 1) }
    }

    @Test
    fun `getCardsFromList should throw IllegalArgumentException if invalid parameters provided`() {
        assertFailsWith<IllegalArgumentException> { services.lists.getListCards(token, 0) }
        assertFailsWith<IllegalArgumentException> { services.lists.getListCards("abc", 0) }
        assertFailsWith<IllegalArgumentException> { services.lists.getListCards("", 1) }
    }

    @Test
    fun `deleteList should delete the list by id`() {
        services.boards.createBoard(token, "Board 1", "Description 1")
        val name = "List 1"
        val boardId = 1
        val id = services.lists.createList(token, name, boardId)
        services.lists.deleteList(token, id)
        assertFailsWith<NotFoundException> { services.lists.getList(token, id) }
    }

    @Test
    fun `deleteList should throw IllegalArgumentException if invalid parameters provided`() {
        assertFailsWith<IllegalArgumentException> { services.lists.deleteList(token, 0) }
        assertFailsWith<IllegalArgumentException> { services.lists.deleteList("abc", 0) }
        assertFailsWith<IllegalArgumentException> { services.lists.deleteList("", 1) }
    }

    @Test
    fun `moveList should move the list to the specified index`() {
        services.boards.createBoard(token, "Board 1", "Description 1")
        val name = "List 1"
        val boardId = 1
        val id = services.lists.createList(token, name, boardId)
        services.lists.moveList(token, id, 1)
        val list = services.lists.getList(token, id)
        assertEquals(1, list.index)
    }

    @Test
    fun `moveList should throw IllegalArgumentException if invalid parameters provided`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Description 1")
        val listId = services.lists.createList(token, "List 1", boardId)
        assertFailsWith<IllegalArgumentException> { services.lists.moveList(token, 0, 1) }
        assertFailsWith<IllegalArgumentException> { services.lists.moveList("abc", 0, 1) }
        assertFailsWith<IllegalArgumentException> { services.lists.moveList("", listId, 1) }
        assertFailsWith<IllegalArgumentException> { services.lists.moveList(token, listId, -1) }
    }

    @Test
    fun `archive a list should archive the list by id`() {
        services.boards.createBoard(token, "Board 1", "Description 1")
        val name = "List 1"
        val boardId = 1
        val id = services.lists.createList(token, name, boardId)
        services.lists.updateList(token, id, null, null, true)
        assertEquals(true, services.lists.getList(token, id).archived)
    }

    @Test
    fun `updateList should update the list by id`() {
        services.boards.createBoard(token, "Board 1", "Description 1")
        val name = "List 1"
        val boardId = 1
        val id = services.lists.createList(token, name, boardId)
        services.lists.updateList(token, id, "List 2", null, null)
        assertEquals("List 2", services.lists.getList(token, id).name)
    }

    @Test
    fun `updateList should throw IllegalArgumentException if invalid parameters provided`() {
        val boardId = services.boards.createBoard(token, "Board 1", "Description 1")
        val listId = services.lists.createList(token, "List 1", boardId)
        assertFailsWith<IllegalArgumentException> { services.lists.updateList(token, 0, "List 2", null, null) }
        assertFailsWith<IllegalArgumentException> { services.lists.updateList("abc", 0, "List 2", null, null) }
        assertFailsWith<IllegalArgumentException> { services.lists.updateList("", 1, "List 2", null, null) }
        assertFailsWith<IllegalArgumentException> { services.lists.updateList(token, listId, "", null, null) }
    }
}
