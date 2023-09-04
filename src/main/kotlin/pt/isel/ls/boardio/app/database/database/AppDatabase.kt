package pt.isel.ls.boardio.app.database.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.boardio.app.database.Database
import pt.isel.ls.boardio.app.database.source.DatabaseSource
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.SearchQuery
import pt.isel.ls.boardio.app.utils.SearchResult
import pt.isel.ls.boardio.app.utils.subSequence
import pt.isel.ls.boardio.domain.boards.database.BoardsDatabase
import pt.isel.ls.boardio.domain.cards.database.CardsDatabase
import pt.isel.ls.boardio.domain.lists.database.ListsDatabase
import pt.isel.ls.boardio.domain.users.database.UsersDatabase

class AppDatabase(private val jdbcDatabaseURL: String) : Database {

    override val users = UsersDatabase()
    override val boards = BoardsDatabase()
    override val lists = ListsDatabase()
    override val cards = CardsDatabase()

    override fun <T> fetch(callback: (Source) -> T): T {
        val dataSource = PGSimpleDataSource().also { it.setURL(jdbcDatabaseURL) }
        dataSource.connection.use { conn ->
            conn.beginTransaction()
            return callback(DatabaseSource(conn)).also { conn.commitTransaction() }
        }
    }

    override fun search(token: String, search: SearchQuery): Pair<List<SearchResult>, Int> {
        val types = search.types?.joinToString(",") { "'$it'" } ?: "'users', 'boards', 'lists', 'cards'"
        val sortBy = when (search.sortBy) {
            "len" -> "length(name)"
            "created" -> "createdDate"
            else -> "name"
        }
        val stm = """
            select name, type, id, createdDate
            from (
              select "user".id, "user".name as name, "user".createdDate, 'users' as type
              from "user"
              
              union
              
              select board.id, board.name, board.createdDate, 'boards' as type
              from board
              join userBoard on board.id = userBoard.boardId
              where userBoard.userid in (
                select id from "user" where token = '$token'
              )
              
              union
              
              select list.id, list.name, list.createdDate, 'lists' as type
              from list
              join board on list.boardId = board.id
              join userBoard on board.id = userBoard.boardId
              where userBoard.userid in (
                select id from "user" where token = '$token'
              )
              
              union
              
              select card.id, card.name, card.createdDate, 'cards' as type
              from card
              join list on card.listId = list.id
              join board on list.boardId = board.id
              join userBoard on board.id = userBoard.boardId
              where userBoard.userid in (
                select id from "user" where token = '$token'
              )
            ) as results
            where name ilike '%${search.query}%' and type in ($types)
            order by $sortBy ${search.orderBy ?: "asc"}
        """.trimIndent()
        val queries = fetch { it.conn.executeQueries(stm) }
        val results = queries.subSequence(search.skip, search.limit).map { result ->
            val (name, type, id, createdDate) = result
            SearchResult(type, id.toInt(), name, createdDate)
        }
        return Pair(results, queries.size)
    }

    override fun reset() {
        listOf("\"user\"", "board", "list", "card", "userBoard").forEach { table ->
            fetch { it.conn.execute("delete from $table") }
        }
    }
}
