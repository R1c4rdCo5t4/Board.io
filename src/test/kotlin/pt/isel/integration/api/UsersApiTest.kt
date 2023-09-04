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
import pt.isel.ls.boardio.app.services.utils.validateToken
import pt.isel.ls.boardio.domain.users.User
import pt.isel.ls.boardio.domain.users.api.CreateUserResponse
import pt.isel.ls.boardio.domain.users.api.LoginUserResponse
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsersApiTest : IntegrationTest() {

    @Test
    fun `Create new user with valid parameters`() {
        val request = Request(POST, "$baseURL/users").json(
            "name" to "R1c4rdCo5t44",
            "email" to "r1c4rdco5t4@isel.pt",
            "password" to "12345678"
        )
        val response = client(request)
        val user = response.decodeAs<CreateUserResponse>()
        assertEquals(Status.CREATED, response.status)
        assertTrue(validateId(user.userId))
        assertTrue(validateToken(user.token))
    }

    @Test
    fun `Create new user with invalid parameters`() {
        val request = Request(POST, "$baseURL/users").json(
            "name" to "wartuga",
            "email" to "invalid email",
            "password" to "123456"
        )
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `Get user with valid id`() {
        val user = createTestUser()
        val request = Request(GET, "$baseURL/users/${user.id}").setToken(token)
        val response = client(request)
        val getUserResponse = response.decodeAs<User>()
        assertEquals(Status.OK, response.status)
        assertEquals(user.name, getUserResponse.name)
        assertEquals(user.email, getUserResponse.email)
    }

    @Test
    fun `Get user with invalid id`() {
        val request = Request(GET, "$baseURL/users/abc")
        val response = client(request)
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `Get user with non existing user`() {
        val request = Request(GET, "$baseURL/users/99")
        val response = client(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `Get all users`() {
        val user = createTestUser()
        val request = Request(GET, "$baseURL/users")
        val response = client(request)
        val users = response.decodeAs<List<User>>()
        assertEquals(Status.OK, response.status)
        assertTrue(users.any { it.id == user.id })
    }

    @Test
    fun `delete user with valid token`() {
        val user = createTestUser()
        val deleteUserRequest = Request(DELETE, "$baseURL/users").setToken(user.token).json("password" to user.password)
        val deleteUserResponse = client(deleteUserRequest)
        assertEquals(Status.OK, deleteUserResponse.status)

        val getDeletedUserRequest = Request(GET, "$baseURL/users/${user.id}")
        val getDeletedUserResponse = client(getDeletedUserRequest)
        assertEquals(Status.NOT_FOUND, getDeletedUserResponse.status)
    }

    @Test
    fun `update user with valid id`() {
        val user = createTestUser()
        val newUsername = "R1c4rdCo5t4"
        val newEmail = "rcosta@isel.pt"

        val updateUserRequest = Request(PATCH, "$baseURL/users").setToken(user.token).json(
            "name" to newUsername,
            "email" to newEmail,
            "password" to user.password
        )
        val updateUserResponse = client(updateUserRequest)
        assertEquals(Status.OK, updateUserResponse.status)

        val getUpdatedUserRequest = Request(GET, "$baseURL/users/${user.id}")
        val getUpdatedUserResponse = client(getUpdatedUserRequest)
        val updatedUser = getUpdatedUserResponse.decodeAs<User>()

        assertEquals(Status.OK, getUpdatedUserResponse.status)
        assertEquals(newUsername, updatedUser.name)
        assertEquals(newEmail, updatedUser.email)
    }

    @Test
    fun `login user with valid credentials`() {
        val user = createTestUser()
        val request = Request(POST, "$baseURL/users/login").json(
            "email" to user.email,
            "password" to user.password
        ).setToken(user.token)
        val response = client(request)
        val loginUserResponse = response.decodeAs<LoginUserResponse>()
        assertEquals(Status.OK, response.status)
        assertEquals(user.id, loginUserResponse.userId)
        assertTrue(validateToken(loginUserResponse.token))
    }

    @Test
    fun `get user by token`() {
        val user = createTestUser()
        val request = Request(GET, "$baseURL/users/token/${user.token}").setToken(user.token)
        val response = client(request)
        val respondeUser = response.decodeAs<User>()
        assertEquals(Status.OK, response.status)
        assertEquals(user.id, respondeUser.id)
    }

    @Test
    fun `get user by username`() {
        val user = createTestUser()
        val request = Request(GET, "$baseURL/users/name/${user.name}").setToken(user.token)
        val response = client(request)
        val respondeUser = response.decodeAs<User>()
        assertEquals(Status.OK, response.status)
        assertEquals(user.id, respondeUser.id)
    }
}
