package pt.isel.ls.boardio.domain.lists.database

import pt.isel.ls.boardio.app.database.database.checkIfAlreadyExists
import pt.isel.ls.boardio.app.database.database.checkIfNotExists
import pt.isel.ls.boardio.app.database.database.execute
import pt.isel.ls.boardio.app.database.database.executeQueries
import pt.isel.ls.boardio.app.database.database.executeQuery
import pt.isel.ls.boardio.app.database.database.getCard
import pt.isel.ls.boardio.app.database.database.getList
import pt.isel.ls.boardio.app.database.database.getListBoardId
import pt.isel.ls.boardio.app.database.database.getNewListIndex
import pt.isel.ls.boardio.app.database.database.getNextIndex
import pt.isel.ls.boardio.app.database.database.getValuesToUpdate
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.subSequence
import pt.isel.ls.boardio.domain.cards.Card
import pt.isel.ls.boardio.domain.lists.List

class ListsDatabase : ListsSource {

    override fun createList(source: Source, name: String, boardId: Int): Int {
        source.conn.checkIfAlreadyExists("list", "name", name, "boardId", boardId) { "List with name $name already exists in board" }
        val index = source.conn.getNextIndex("index", "list", "where boardId = $boardId")
        val stm = "insert into list(name, boardId, index) values (?,?,?) returning id"
        val args = listOf(name, boardId, index)
        val column = listOf("id")
        val (listId) = source.conn.executeQuery(stm, args, column)
        return listId.toInt()
    }

    override fun getList(source: Source, listId: Int): List {
        val stm = "select id, name, boardId, index, archived, createdDate from list where id = ?"
        val args = listOf(listId)
        val query = source.conn.executeQuery(stm, args) { "List with id $listId not found" }
        return source.conn.getList(query)
    }

    override fun getListCards(source: Source, listId: Int, skip: Int?, limit: Int?): kotlin.collections.List<Card> {
        source.conn.checkIfNotExists("list", "id", listId) { "List with id $listId not found" }
        val stm = "select id, name, description, listId, index, dueDate, archived, createdDate from card where listId = ? order by index"
        val args = listOf(listId)
        val queries = source.conn.executeQueries(stm, args)
        return queries.map { source.conn.getCard(it) }.subSequence(skip, limit)
    }

    override fun getBoardIdOfList(source: Source, listId: Int): Int {
        val stm = "select boardId from list where id = ?"
        val args = listOf(listId)
        val (boardId) = source.conn.executeQuery(stm, args) { "List with id $listId not found" }
        return boardId.toInt()
    }

    override fun deleteList(source: Source, listId: Int) {
        source.conn.checkIfNotExists("list", "id", listId) { "List with id $listId not found" }
        val stm = "delete from list where id = ?"
        val args = listOf(listId)
        source.conn.execute(stm, args)
    }

    override fun moveList(source: Source, listId: Int, index: Int) {
        source.conn.checkIfNotExists("list", "id", listId) { "List with id $listId not found" }
        val destIndex = source.conn.getNewListIndex(index, listId)
        val updateStm = "update list set index = ? where id = ?"
        val updateArgs = listOf(destIndex, listId)
        source.conn.execute(updateStm, updateArgs)
    }

    override fun updateList(source: Source, listId: Int, name: String?, index: Int?, archived: Boolean?) {
        val boardId = source.conn.getListBoardId(listId)
        source.conn.checkIfAlreadyExists("list", "name", name, "boardId", boardId) { "List with name $name already exists in board" }
        val (columns, valuesToUpdate) = getValuesToUpdate(
            "name" to name,
            "index" to index,
            "archived" to archived
        )
        val stm = "update list set $columns where id = ?"
        val args = listOf(*valuesToUpdate, listId)
        source.conn.execute(stm, args)
    }
}
