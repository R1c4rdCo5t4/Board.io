package pt.isel.ls.boardio.app.api

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.boardio.app.AppServer.Companion.logger
import pt.isel.ls.boardio.app.api.utils.error
import pt.isel.ls.boardio.app.api.utils.getOptionalIntQuery
import pt.isel.ls.boardio.app.api.utils.getOptionalQuery
import pt.isel.ls.boardio.app.api.utils.getQuery
import pt.isel.ls.boardio.app.api.utils.getToken
import pt.isel.ls.boardio.app.api.utils.json
import pt.isel.ls.boardio.app.services.Services
import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.ls.boardio.app.utils.SearchResponse
import pt.isel.ls.boardio.app.utils.log
import pt.isel.ls.boardio.app.utils.status
import pt.isel.ls.boardio.domain.boards.api.BoardsApi
import pt.isel.ls.boardio.domain.cards.api.CardsApi
import pt.isel.ls.boardio.domain.lists.api.ListsApi
import pt.isel.ls.boardio.domain.users.api.UsersApi

class AppApi(val services: Services) : Api {
    override val users = UsersApi(services.users)
    override val boards = BoardsApi(services.boards)
    override val lists = ListsApi(services.lists)
    override val cards = CardsApi(services.cards)
    override val routes = routes(
        "/users" bind users.routes,
        "/boards" bind boards.routes,
        "/lists" bind lists.routes,
        "/cards" bind cards.routes,
        "/search" bind request { search(it) }
    )

    companion object {
        fun request(handler: (Request) -> Response): HttpHandler = { request ->
            try {
                logger.log(request)
                handler(request).also { response -> logger.log(response) }
            } catch (e: Exception) {
                logger.log(e)
                Response(e.status()).error(e)
            }
        }
    }

    override fun search(request: Request): Response {
        val token = request.getToken()
        val query = request.getQuery("query")
        val type = request.getOptionalQuery("type")?.split(",") ?: listOf("users", "boards", "lists", "cards")
        val sortBy = request.getOptionalQuery("sortby") ?: "name"
        val orderBy = request.getOptionalQuery("orderby") ?: "asc"
        val skip = request.getOptionalIntQuery("skip")
        val limit = request.getOptionalIntQuery("limit")
        val searchQuery = SearchQuery(query, type, sortBy, orderBy, skip, limit)
        val (results, total) = services.search(token, searchQuery)
        val response = SearchResponse(results, total)
        return Response(Status.OK).json(response)
    }
}
