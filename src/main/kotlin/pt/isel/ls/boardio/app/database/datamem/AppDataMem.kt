package pt.isel.ls.boardio.app.database.datamem

import pt.isel.ls.boardio.app.database.Database
import pt.isel.ls.boardio.app.database.source.MemSource
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.ls.boardio.app.utils.SearchResult
import pt.isel.ls.boardio.app.utils.subSequence
import pt.isel.ls.boardio.domain.boards.database.BoardsDataMem
import pt.isel.ls.boardio.domain.cards.database.CardsDataMem
import pt.isel.ls.boardio.domain.lists.database.ListsDataMem
import pt.isel.ls.boardio.domain.users.database.UsersDataMem

class AppDataMem : Database {

    private val source = MemSource()
    override val users = UsersDataMem()
    override val boards = BoardsDataMem()
    override val lists = ListsDataMem()
    override val cards = CardsDataMem()

    override fun <T> fetch(callback: (Source) -> T): T {
        return callback(source)
    }

    override fun search(token: String, search: SearchQuery): Pair<List<SearchResult>, Int> {
        val user = fetch { users.getUserByToken(it, token) }
        val queryUsers = fetch { users.getUsers(it) }
        val queryBoards = fetch { boards.getUserBoards(it, user.id) }
        val queryLists = queryBoards.flatMap { b -> fetch { boards.getBoardLists(it, b.id) } }
        val queryCards = queryLists.flatMap { l -> fetch { lists.getListCards(it, l.id) } }

        val queriesByType = mapOf(
            "users" to queryUsers,
            "boards" to queryBoards,
            "lists" to queryLists,
            "cards" to queryCards
        )
        val queries = queriesByType.filter { search.types?.contains(it.key) == true || search.types == null }.values.flatten()
        val results = queries
            .subSequence(search.skip, search.limit)
            .map { SearchResult(it::class.java.simpleName.lowercase(), it.id, it.name, it.createdDate.toString()) }
            .filter { it.name.contains(search.query, true) }
            .sortedWith(
                compareBy {
                    when (search.sortBy) {
                        "createdDate" -> it.createdDate
                        else -> it.name
                    }
                }
            ).let {
                when (search.orderBy) {
                    "desc" -> it.reversed()
                    else -> it
                }
            }
        return Pair(results, queries.size)
    }

    override fun reset() {
        source.mem.domain.forEach { it.clear() }
    }
}
