package pt.isel.ls.boardio.app.database

import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.ls.boardio.app.utils.SearchResult
import pt.isel.ls.boardio.domain.boards.database.BoardsSource
import pt.isel.ls.boardio.domain.cards.database.CardsSource
import pt.isel.ls.boardio.domain.lists.database.ListsSource
import pt.isel.ls.boardio.domain.users.database.UsersSource

interface Database {
    val users: UsersSource
    val boards: BoardsSource
    val lists: ListsSource
    val cards: CardsSource

    fun <T> fetch(callback: (Source) -> T): T
    fun search(token: String, search: SearchQuery): Pair<List<SearchResult>, Int>
    fun reset()
}
