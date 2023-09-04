package pt.isel.ls.boardio.domain.boards.api

import kotlinx.serialization.Serializable

@Serializable
data class CreateBoardRequest(val name: String, val description: String)

@Serializable
data class UpdateBoardRequest(val name: String? = null, val description: String? = null)

@Serializable
data class CreateBoardResponse(val id: Int)

@Serializable
data class GetBoardUserResponse(val name: String, val email: String, val id: Int)

@Serializable
data class RemoveUserFromBoardResponse(val message: String, val deletedBoard: Boolean)
