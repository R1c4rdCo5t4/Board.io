package pt.isel.unit.services

import org.junit.Test
import pt.isel.ls.boardio.app.utils.AlreadyExistsException
import pt.isel.ls.boardio.app.utils.ForbiddenException
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.unit.UnitTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class UsersUnitTest : UnitTest() {

    @Test
    fun `createUser should create a user with a unique id and token and return them`() {
        val name = "User 1"
        val email = "user1@test.kt"
        val password = "123456"
        val (id, token) = services.users.createUser(name, email, password)
        assertEquals(2, id)
        assertNotNull(token)
        assertIs<String>(token)
        assertEquals(36, token.length)
    }

    @Test
    fun `createUser should throw IllegalArgumentException when given invalid parameters`() {
        val name = ""
        val email = "user1@test.kt"
        val password = ""
        assertFailsWith<IllegalArgumentException> { services.users.createUser(name, email, password) }
    }

    @Test
    fun `getUser should get user by id`() {
        val name = "user 1"
        val email = "user1@gmail.com"
        val password = "123456"
        val (id, token) = services.users.createUser(name, email, password)
        val user = services.users.getUserById(id)
        assertEquals(name, user.name)
        assertEquals(email, user.email)
        assertEquals(token, user.token)
    }

    @Test
    fun `getUser should throw IllegalArgumentException when given invalid parameters`() {
        assertFailsWith<IllegalArgumentException> { services.users.getUserById(0) }
    }

    @Test
    fun `getUsers should return all users`() {
        val users = services.users.getUsers()
        assertEquals(1, users.size)
    }

    @Test
    fun `delete user with valid token`() {
        val name = "user 1"
        val email = "user1@gmail.com"
        val password = "123456"
        val (id, token) = services.users.createUser(name, email, password)
        services.users.deleteUser(token, password)
        assertFailsWith<NotFoundException> { services.users.getUserByToken(token) }
    }

    @Test
    fun `delete user with invalid token`() {
        val name = "user 1"
        val email = "user1@gmail.com"
        val password = "123456"
        val (id, validToken) = services.users.createUser(name, email, password)
        val validPassword = "123456789"
        assertFailsWith<IllegalArgumentException> { services.users.deleteUser("invalid token", validPassword) }
        assertFailsWith<IllegalArgumentException> { services.users.deleteUser(validToken, "") }
    }

    @Test
    fun `update user with valid id`() {
        val name = "user 1"
        val email = "user1@gmail.com"
        val password = "123456"
        val (id, token) = services.users.createUser(name, email, password)
        val newName = "user 2"
        val newEmail = "user2@gmail.com"
        val newPassword = "123456789"
        services.users.updateUser(token, password, newName, newEmail, newPassword)
        val user = services.users.getUserByToken(token)
        assertEquals(newName, user.name)
        assertEquals(newEmail, user.email)
    }

    @Test
    fun `update user with invalid id`() {
        val name = "user 1"
        val email = "user1@gmail.com"
        val password = "1234567"
        val (id, token) = services.users.createUser(name, email, password)
        val newName = "user 2"
        val newEmail = "user2@gmail.com"
        val newPassword = "123456789"
        assertFailsWith<IllegalArgumentException> {
            services.users.updateUser(
                "invalid-token",
                password,
                newName,
                newEmail,
                newPassword
            )
        }
    }

    @Test
    fun `update user with already existing username`() {
        val name = "user 1"
        val email = "user1@gmail.com"
        val password = "1234567"
        services.users.createUser(name, email, password)
        val name1 = "user 2"
        val email1 = "user2@gmail.com"
        val (id1, token1) = services.users.createUser(name1, email1, password)
        assertFailsWith<AlreadyExistsException> {
            services.users.updateUser(
                token1,
                password,
                name,
                email1,
                null
            )
        }
    }

    @Test
    fun `login user with valid credentials`() {
        val name = "user 1"
        val email = "user1@gmail.com"
        val password = "12345678"
        services.users.createUser(name, email, password)
        val (id, token) = services.users.loginUser(email, password)
        assertNotNull(token)
        assertIs<String>(token)
        assertEquals(36, token.length)
    }

    @Test
    fun `login user with invalid credentials`() {
        val name = "user 1"
        val email = "user1@gmail.com"
        val password = "12345678"
        services.users.createUser(name, email, password)
        assertFailsWith<ForbiddenException> { services.users.loginUser(email, "incorrect password") }
        assertFailsWith<NotFoundException> { services.users.loginUser("invalid email", password) }
    }

    @Test
    fun `getUserByName should return user with valid name`() {
        val user = createTestUser()
        val userByName = services.users.getUserByName(user.name)
        assertEquals(user.id, userByName.id)
        assertEquals(user.name, userByName.name)
        assertEquals(user.email, userByName.email)
        assertEquals(user.token, userByName.token)
    }

    @Test
    fun `getUserByToken should return user with valid token`() {
        val user = createTestUser()
        val userByToken = services.users.getUserByToken(user.token)
        assertEquals(user.id, userByToken.id)
        assertEquals(user.name, userByToken.name)
        assertEquals(user.email, userByToken.email)
        assertEquals(user.token, userByToken.token)
    }
}
