package pt.isel.unit.services

import org.junit.Test
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.app.utils.UnauthorizedException
import pt.isel.unit.UnitTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BoardsUnitTest : UnitTest() {

    @Test
    fun `createBoard should create a board with a unique id and return it`() {
        val name = "Board 1"
        val description = "Description 1"
        val id = services.boards.createBoard(token, name, description)
        assertEquals(1, id)
    }

    @Test
    fun `createBoard should throw IllegalArgumentException if invalid parameters provided`() {
        val invalidName = ""
        val validName = "Board 1"
        val validDescription = "description"
        val invalidToken = ""
        assertFailsWith<IllegalArgumentException> { services.boards.createBoard(token, invalidName, validDescription) }
        assertFailsWith<IllegalArgumentException> { services.boards.createBoard(invalidToken, validName, validDescription) }
    }

    @Test
    fun `getBoard should return the board by id`() {
        val name = "Board 1"
        val description = "Description 1"
        val id = services.boards.createBoard(token, name, description)
        val board = services.boards.getBoard(token, id)
        assertEquals(name, board.name)
        assertEquals(description, board.description)
    }

    @Test
    fun `getBoard should throw IllegalArgumentException if invalid parameters provided`() {
        assertFailsWith<IllegalArgumentException> { services.boards.getBoard(token, 0) }
        assertFailsWith<IllegalArgumentException> { services.boards.getBoard("abc", 1) }
    }

    @Test
    fun `getUserBoards should get the boards of the User Test`() {
        var userBoards = services.boards.getUserBoards(token)
        assertEquals(0, userBoards.size)
        val name = "Board 1"
        val description = "Description 1"
        services.boards.createBoard(token, name, description)
        userBoards = services.boards.getUserBoards(token)
        assertEquals(1, userBoards.size)
    }

    @Test
    fun `listUsersOfBoard should get the users of the board`() {
        val name = "Board 1"
        val description = "Description 1"
        val id = services.boards.createBoard(token, name, description)
        val users = services.boards.getBoardUsers(token, id)
        assertEquals(1, users.size)
    }

    @Test
    fun `getUserBoards should throw NotFoundException because the user does not exist`() {
        assertFailsWith<UnauthorizedException> { services.boards.getUserBoards("ee897bc5-559f-49c6-9f71-0cd87b59af95") }
    }

    @Test
    fun `getUserBoards should throw IllegalArgumentException when given invalid parameters`() {
        assertFailsWith<IllegalArgumentException> { services.boards.getUserBoards("") }
        assertFailsWith<IllegalArgumentException> { services.boards.getUserBoards("abc") }
    }

    @Test
    fun `getListsOfBoard should throw NotFoundException because the board id does not exist`() {
        assertFailsWith<NotFoundException> { services.boards.getBoardLists(token, 99) }
    }

    @Test
    fun `getListsOfBoard should throw IllegalArgumentException if invalid parameters provided`() {
        assertFailsWith<IllegalArgumentException> { services.boards.getBoardLists(token, 0) }
        assertFailsWith<IllegalArgumentException> { services.boards.getBoardLists("abc", 1) }
    }

    @Test
    fun `listUsersOfBoard should throw NotFoundException because the board id does not exist`() {
        assertFailsWith<NotFoundException> { services.boards.getBoardUsers(token, 23) }
    }

    @Test
    fun `Update board should return true if the board is updated`() {
        val name = "Board 1"
        val description = "Description 1"
        val id = services.boards.createBoard(token, name, description)
        services.boards.updateBoard(token, id, "Board 2", "Description 2")
        val board = services.boards.getBoard(token, id)
        assertEquals("Board 2", board.name)
        assertEquals("Description 2", board.description)
    }

    @Test
    fun `Update board should throw NotFoundException because the board id does not exist`() {
        assertFailsWith<NotFoundException> { services.boards.updateBoard(token, 23, "Board 2", "Description 2") }
    }

    @Test
    fun `Delete board should return true if the board is deleted`() {
        val name = "Board 1"
        val description = "Description 1"
        val id = services.boards.createBoard(token, name, description)
        services.boards.deleteBoard(token, id)
        assertFailsWith<NotFoundException> { services.boards.getBoard(token, id) }
    }

    @Test
    fun `Delete board should throw NotFoundException because the board id does not exist`() {
        assertFailsWith<NotFoundException> { services.boards.deleteBoard(token, 23) }
    }

    @Test
    fun `addUserToBoard should add user to board specified`() {
        services.boards.createBoard(token, "Board 1", "Description 1")
        services.boards.addUserToBoard(token, 1, 1)
    }

    @Test
    fun `addUserToBoard should throw IllegalArgumentException when given invalid parameters`() {
        assertFailsWith<IllegalArgumentException> { services.boards.addUserToBoard("", 0, 1) }
        assertFailsWith<IllegalArgumentException> { services.boards.addUserToBoard("abc", 2, 0) }
    }

    @Test
    fun `removeUserFromBoard should remove the user from the board`() {
        services.boards.createBoard(token, "Board 1", "Description 1")
        services.boards.removeUserFromBoard(token, 1, 1)
    }

    @Test
    fun `removeUserFromBoard should throw IllegalArgumentException when given invalid parameters`() {
        assertFailsWith<IllegalArgumentException> { services.boards.removeUserFromBoard("", 0, -1) }
        assertFailsWith<IllegalArgumentException> { services.boards.removeUserFromBoard("token", 2, 0) }
    }
}
