package pt.isel.ls.boardio.domain.users.api

import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PATCH
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.boardio.app.api.AppApi.Companion.request
import pt.isel.ls.boardio.app.api.utils.decodeAs
import pt.isel.ls.boardio.app.api.utils.getIntParameter
import pt.isel.ls.boardio.app.api.utils.getParameter
import pt.isel.ls.boardio.app.api.utils.getToken
import pt.isel.ls.boardio.app.api.utils.json
import pt.isel.ls.boardio.app.api.utils.message
import pt.isel.ls.boardio.domain.users.services.UsersServices

class UsersApi(private val services: UsersServices) {

    val routes = routes(
        "/" bind GET to request { getUsers() },
        "/" bind POST to request { createUser(it) },
        "/" bind PATCH to request { updateUser(it) },
        "/" bind DELETE to request { deleteUser(it) },
        "/{userId}" bind GET to request { getUser(it) },
        "/login" bind POST to request { loginUser(it) },
        "/token/{token}" bind GET to request { getUserByToken(it) },
        "/name/{name}" bind GET to request { getUserByName(it) }
    )

    private fun createUser(request: Request): Response {
        val user = request.decodeAs<CreateUserRequest>()
        val (id, token) = services.createUser(user.name, user.email.lowercase(), user.password)
        val response = CreateUserResponse(id, token)
        return Response(CREATED).json(response)
    }

    private fun loginUser(request: Request): Response {
        val user = request.decodeAs<LoginUserRequest>()
        val (id, token) = services.loginUser(user.email.lowercase(), user.password)
        val response = LoginUserResponse(id, token)
        return Response(OK).json(response)
    }

    private fun getUser(request: Request): Response {
        val userId = request.getIntParameter("userId")
        val user = services.getUserById(userId)
        return Response(OK).json(user)
    }

    private fun getUserByName(request: Request): Response {
        val name = request.getParameter("name")
        val user = services.getUserByName(name)
        return Response(OK).json(user)
    }

    private fun getUserByToken(request: Request): Response {
        val token = request.getParameter("token")
        val user = services.getUserByToken(token)
        return Response(OK).json(user)
    }

    private fun getUsers(): Response {
        val users = services.getUsers()
        return Response(OK).json(users)
    }

    private fun updateUser(request: Request): Response {
        val token = request.getToken()
        val user = request.decodeAs<UpdateUserRequest>()
        services.updateUser(token, user.password, user.name, user.email, user.newPassword)
        return Response(OK).message("User updated successfully")
    }

    private fun deleteUser(request: Request): Response {
        val token = request.getToken()
        val password = request.decodeAs<DeleteUserRequest>().password
        services.deleteUser(token, password)
        return Response(OK).message("User deleted successfully")
    }
}
