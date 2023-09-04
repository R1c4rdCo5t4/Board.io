package pt.isel.ls.boardio.app.utils

import kotlinx.serialization.Serializable
import pt.isel.ls.boardio.app.services.utils.validateString

@Serializable
data class SearchQuery(
    val query: String,
    val types: List<String>? = null,
    val sortBy: String? = null,
    val orderBy: String? = null,
    val skip: Int? = null,
    val limit: Int? = null
)

@Serializable
data class SearchResult(
    val type: String,
    val id: Int,
    val name: String,
    val createdDate: String
)

@Serializable
data class SearchResponse(
    val results: List<SearchResult>,
    val total: Int
)

fun validateType(type: List<String>?) {
    if (type != null) {
        type.forEach { validateString(it) }
        val values = listOf("users", "boards", "lists", "cards")
        require(type.all { it in values }) { "Invalid type value: $type" }
    }
}

fun validateSortBy(sortBy: String?) {
    if (sortBy != null) {
        validateString(sortBy)
        val possibleValues = listOf("name", "created", "len")
        require(sortBy in possibleValues) { "Invalid sortBy value: $sortBy" }
    }
}

fun validateOrderBy(orderBy: String?) {
    if (orderBy != null) {
        validateString(orderBy)
        val possibleValues = listOf("asc", "desc")
        require(orderBy in possibleValues) { "Invalid orderBy value: $orderBy" }
    }
}
