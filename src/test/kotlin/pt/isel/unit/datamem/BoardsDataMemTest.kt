package pt.isel.unit.datamem

import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.unit.UnitTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BoardsDataMemTest : UnitTest() {

    @Test
    fun `createBoard should create a board with a unique id and return it`() {
        val name = "Board 1"
        val description = "Description 1"
        val id = mem.fetch { mem.boards.createBoard(it, name, description) }
        assertEquals(1, id)

        val board = mem.fetch { it.mem.boards[id] }
        requireNotNull(board)
        assertEquals(name, board.name)
        assertEquals(description, board.description)
    }

    @Test
    fun `getBoard should return the board with the given id`() {
        val name = "Board 1"
        val description = "Description 1"
        val id = mem.fetch { mem.boards.createBoard(it, name, description) }
        val board = mem.fetch { mem.boards.getBoard(it, id) }
        assertEquals(name, board.name)
        assertEquals(description, board.description)
    }

    @Test
    fun `getBoard should throw NotFoundException because the given id does not exist`() {
        val id = 99 // this id does not exist
        assertFailsWith<NotFoundException> {
            mem.fetch { mem.boards.getBoard(it, id) }
        }
    }

    @Test
    fun `getUserBoards should return the list of boards that the user is in`() {
        val user = createTestUser()
        val board1 = createTestBoard()
        val board2 = createTestBoard()
        mem.fetch { mem.boards.addUserToBoard(it, user.id, board1.id) }
        mem.fetch { mem.boards.addUserToBoard(it, user.id, board2.id) }

        val boards = mem.fetch { mem.boards.getUserBoards(it, user.id) }
        assertEquals(2, boards.size)
        assertContains(boards, board1)
        assertContains(boards, board2)
    }

    @Test
    fun `getUserBoards should throw NotFoundException because the user id does not exist`() {
        val userId = 99 // this user id does not exist
        assertFailsWith<NotFoundException> {
            mem.fetch { mem.boards.getUserBoards(it, userId) }
        }
    }

    @Test
    fun `Second user should not be able to get the boards of the first user`() {
        val user1 = createTestUser()
        val user2 = createTestUser()
        val board1 = createTestBoard()
        val board2 = createTestBoard()
        val board3 = createTestBoard()

        val boards = mem.fetch {
            mem.boards.addUserToBoard(it, user1.id, board1.id)
            mem.boards.addUserToBoard(it, user1.id, board2.id)
            mem.boards.addUserToBoard(it, user2.id, board3.id)
            mem.boards.getUserBoards(it, user2.id)
        }
        assertEquals(1, boards.size)
        assertFalse(boards.contains(board1))
        assertFalse(boards.contains(board2))
        assertTrue(boards.contains(board3))
    }

    @Test
    fun `getBoardLists should return all lists of the board with the given id`() {
        val board = createTestBoard()
        val list1 = createTestList(board.id)
        val list2 = createTestList(board.id)
        val lists = mem.fetch { mem.boards.getBoardLists(it, board.id) }
        assertEquals(2, lists.size)
        assertContains(lists, list1)
        assertContains(lists, list2)
    }

    @Test
    fun `getBoardLists should throw NotFounException because the board id does not exist`() {
        val boardId = 99 // this board does not exist
        assertFailsWith<NotFoundException> {
            mem.fetch { mem.boards.getBoardLists(it, boardId) }
        }
    }

    @Test
    fun `getBoardUserIds should return all user ids that are in the board with the given id`() {
        val user1 = createTestUser()
        val user2 = createTestUser()
        val board = createTestBoard()

        val users = mem.fetch {
            mem.boards.addUserToBoard(it, user1.id, board.id)
            mem.boards.addUserToBoard(it, user2.id, board.id)
            mem.boards.getBoardUserIds(it, board.id)
        }
        assertEquals(3, users.size)
        assertContains(users, user1.id)
        assertContains(users, user2.id)
    }

    @Test
    fun `listUsersOfBoard should throw NotFoundException because the board id does not exist`() {
        val boardId = 99 // this board does not exist
        assertFailsWith<NotFoundException> {
            mem.fetch { mem.boards.getBoardUserIds(it, boardId) }
        }
    }

    @Test
    fun `Update board should update the board`() {
        val board = createTestBoard()
        val newName = "name updated"
        val newDescription = "description updated"
        val updatedBoard = mem.fetch {
            mem.boards.updateBoard(it, board.id, newName, newDescription)
            mem.boards.getBoard(it, board.id)
        }
        assertNotNull(updatedBoard)
        assertEquals(newName, updatedBoard.name)
        assertEquals(newDescription, updatedBoard.description)
    }

    @Test
    fun `Delete board should delete the board`() {
        val board = createTestBoard()
        mem.fetch {
            mem.boards.deleteBoard(it, board.id)
            assertFailsWith<NotFoundException> { mem.boards.getBoard(it, board.id) }
        }
    }

    @Test
    fun `addUserToBoard should add the user to the board by id`() {
        val user = createTestUser()
        val board = createTestBoard()
        mem.fetch {
            mem.boards.addUserToBoard(it, user.id, board.id)
            assertTrue(user.id in mem.boards.getBoardUserIds(it, board.id))
        }
    }

    @Test
    fun `removeUserFromBoard should remove the user from board by id`() {
        val user = createTestUser()
        val board = createTestBoard()
        mem.fetch {
            mem.boards.addUserToBoard(it, user.id, board.id)
            mem.boards.removeUserFromBoard(it, user.id, board.id)
            assertFalse(user.id in mem.boards.getBoardUserIds(it, board.id))
        }
    }

    @Test
    fun `search a valid board`() {
        val board = createTestBoard()
        val (results, total) = mem.search(token, SearchQuery("board", listOf("boards")))
        assertEquals(1, total)
        assertEquals(board.id, results.first().id)
        assertEquals(board.name, results.first().name)
    }
}
