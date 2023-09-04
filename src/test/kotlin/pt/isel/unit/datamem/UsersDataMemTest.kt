package pt.isel.unit.datamem

import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.unit.UnitTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UsersDataMemTest : UnitTest() {

    @Test
    fun `createUser should create a user with a unique id and token and return them`() {
        val username = "r1c4rdo"
        val email = "r1c4rdo@isel.com"
        val password = "123456"
        val user = mem.fetch {
            val id = mem.users.createUser(it, username, email, password).first
            mem.users.getUserById(it, id)
        }
        assertNotNull(user.id)
        assertNotNull(user.token)
        assertEquals(username, user.name)
        assertEquals(email, user.email)
    }

    @Test
    fun `getUser should return the user with the given id`() {
        val testUser = createTestUser()
        val user = mem.fetch { mem.users.getUserById(it, testUser.id) }
        assertEquals(testUser.name, user.name)
        assertEquals(testUser.email, user.email)
    }

    @Test
    fun `getUser should throw NotFoundException because the user with the given id does not exist`() {
        val id = 99 // this id does not exist
        assertFailsWith<NotFoundException> {
            mem.fetch { mem.users.getUserById(it, id) }
        }
    }

    @Test
    fun `Get all users`() {
        val beforeUsers = mem.fetch { mem.users.getUsers(it) }
        createTestUser()
        val afterUsers = mem.fetch { mem.users.getUsers(it) }
        assertEquals(beforeUsers.size + 1, afterUsers.size)
    }

    @Test
    fun `delete user`() {
        val user = createTestUser()
        mem.fetch {
            mem.users.deleteUser(it, user.token, user.password ?: "")
            assertFailsWith<NotFoundException> { mem.users.getUserById(it, user.id) }
        }
    }

    @Test
    fun `update user`() {
        val user = createTestUser()
        val newUsername = "updated user"
        val newEmail = "updated@test.kt"
        val updateUser = mem.fetch {
            mem.users.updateUser(it, user.token, user.password ?: "", newUsername, newEmail)
            mem.users.getUserById(it, user.id)
        }
        assertEquals(newUsername, updateUser.name)
        assertEquals(newEmail, updateUser.email)
    }

    @Test
    fun `login user`() {
        val user = createTestUser()
        val (id, token) = mem.fetch { mem.users.loginUser(it, user.email, user.password ?: "") }
        assertEquals(user.id, id)
        assertEquals(user.token, token)
    }

    @Test
    fun `get user with name`() {
        val user = createTestUser()
        val getUser = mem.fetch { mem.users.getUserByName(it, user.name) }
        assertEquals(user.name, getUser.name)
        assertEquals(user.email, getUser.email)
        assertEquals(user.id, getUser.id)
        assertEquals(user.token, getUser.token)
    }

    @Test
    fun `get user with token`() {
        val user = createTestUser()
        val getUser = mem.fetch { mem.users.getUserByToken(it, user.token) }
        assertEquals(user.name, getUser.name)
        assertEquals(user.email, getUser.email)
        assertEquals(user.id, getUser.id)
        assertEquals(user.token, getUser.token)
    }
}
