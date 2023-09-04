package pt.isel.ls.boardio.app.services

import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.ls.boardio.app.utils.SearchResult
import pt.isel.ls.boardio.domain.boards.services.BoardsServices
import pt.isel.ls.boardio.domain.cards.services.CardsServices
import pt.isel.ls.boardio.domain.lists.services.ListsServices
import pt.isel.ls.boardio.domain.users.services.UsersServices

interface Services {
    val users: UsersServices
    val boards: BoardsServices
    val lists: ListsServices
    val cards: CardsServices

    fun search(token: String, search: SearchQuery): Pair<List<SearchResult>, Int>
}
