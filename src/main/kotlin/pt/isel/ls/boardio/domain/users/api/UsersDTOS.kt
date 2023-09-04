package pt.isel.ls.boardio.domain.users.api

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(val name: String, val email: String, val password: String)

@Serializable
data class CreateUserResponse(val userId: Int, val token: String)

@Serializable
data class LoginUserRequest(val email: String, val password: String)

@Serializable
data class LoginUserResponse(val userId: Int, val token: String)

@Serializable
data class DeleteUserRequest(val password: String)

@Serializable
data class UpdateUserRequest(val password: String, val name: String? = null, val email: String? = null, val newPassword: String? = null)
