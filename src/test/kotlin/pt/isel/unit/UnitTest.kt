package pt.isel.unit

import pt.isel.ls.boardio.app.database.datamem.AppDataMem
import pt.isel.ls.boardio.app.services.AppServices
import pt.isel.ls.boardio.domain.boards.Board
import pt.isel.ls.boardio.domain.cards.Card
import pt.isel.ls.boardio.domain.lists.List
import pt.isel.ls.boardio.domain.users.User
import java.util.UUID

abstract class UnitTest {

    val mem = AppDataMem()
    val services = AppServices(mem)
    val token = createTestUser().token

    fun createTestUser(): User {
        val name = "test-user-${randomId()}"
        val email = "user${randomId()}@test.kt"
        val password = "12345678"
        val user = mem.fetch {
            val id = mem.users.createUser(it, name, email, password).first
            mem.users.getUserById(it, id)
        }
        return User(user.id, user.name, user.email, user.token, password)
    }

    fun createTestBoard(): Board {
        val name = "test-board-${randomId()}"
        val description = "test"
        val id = services.boards.createBoard(token, name, description)
        return services.boards.getBoard(token, id)
    }

    fun createTestList(boardId: Int): List {
        val name = "test-list-${randomId()}"
        val id = services.lists.createList(token, name, boardId)
        return services.lists.getList(token, id)
    }

    fun createTestCard(listId: Int): Card {
        val name = "test-card"
        val description = "test"
        val id = services.cards.createCard(token, name, description, listId)
        return services.cards.getCard(token, id)
    }

    private fun randomId() = UUID.randomUUID().toString().substring(0, 6)
}
