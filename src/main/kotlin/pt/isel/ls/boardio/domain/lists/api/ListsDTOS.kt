package pt.isel.ls.boardio.domain.lists.api

import kotlinx.serialization.Serializable

@Serializable
data class CreateListRequest(val name: String, val boardId: Int)

@Serializable
data class UpdateListRequest(val name: String? = null, val index: Int? = null, val archived: Boolean? = null)

@Serializable
data class CreateListResponse(val id: Int)
