package pt.isel.ls.boardio.app.api

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import pt.isel.ls.boardio.domain.boards.api.BoardsApi
import pt.isel.ls.boardio.domain.cards.api.CardsApi
import pt.isel.ls.boardio.domain.lists.api.ListsApi
import pt.isel.ls.boardio.domain.users.api.UsersApi

interface Api {
    val users: UsersApi
    val boards: BoardsApi
    val lists: ListsApi
    val cards: CardsApi
    val routes: RoutingHttpHandler
    fun search(request: Request): Response
}
