package pt.isel.ls.boardio.app.services

import pt.isel.ls.boardio.app.database.Database
import pt.isel.ls.boardio.app.services.utils.validatePositiveInt
import pt.isel.ls.boardio.app.services.utils.validateString
import pt.isel.ls.boardio.app.services.utils.validateToken
import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.ls.boardio.app.utils.SearchResult
import pt.isel.ls.boardio.app.utils.validateOrderBy
import pt.isel.ls.boardio.app.utils.validateSortBy
import pt.isel.ls.boardio.app.utils.validateType
import pt.isel.ls.boardio.domain.boards.services.BoardsServices
import pt.isel.ls.boardio.domain.cards.services.CardsServices
import pt.isel.ls.boardio.domain.lists.services.ListsServices
import pt.isel.ls.boardio.domain.users.services.UsersServices

class AppServices(private val db: Database) : Services {
    override val users = UsersServices(db)
    override val boards = BoardsServices(db)
    override val lists = ListsServices(db)
    override val cards = CardsServices(db)

    override fun search(token: String, search: SearchQuery): Pair<List<SearchResult>, Int> {
        validateToken(token) { "Invalid user token" }
        validateString(search.query) { "Invalid query" }
        validateType(search.types)
        validateSortBy(search.sortBy)
        validateOrderBy(search.orderBy)
        validatePositiveInt(search.skip) { "Invalid skip value" }
        validatePositiveInt(search.limit) { "Invalid limit value" }
        db.fetch { it.authenticateUser(token) }
        return db.search(token, search)
    }
}
