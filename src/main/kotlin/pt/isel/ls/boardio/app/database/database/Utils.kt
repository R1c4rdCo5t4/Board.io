package pt.isel.ls.boardio.app.database.database

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.AlreadyExistsException
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.app.utils.toLocalDateTime
import pt.isel.ls.boardio.app.utils.toTimestamp
import pt.isel.ls.boardio.domain.cards.Card
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

const val INDEX_GAP = 1000

fun Connection.beginTransaction() {
    check(autoCommit) { "Transaction is already in progress" }
    autoCommit = false
}

fun Connection.commitTransaction() {
    check(!autoCommit) { "Transaction is not in progress" }
    commit()
    autoCommit = true
}

fun Connection.execute(statement: String, args: List<Any?> = emptyList()) {
    val stm = prepareStatement(statement)
    stm.set(*args.toTypedArray())
    stm.execute()
    stm.close()
}

fun Connection.executeQuery(statement: String, args: List<Any?>, columns: List<String> = emptyList(), notFoundMessage: () -> String = { "" }): List<String> {
    val stm = prepareStatement(statement)
    stm.set(*args.toTypedArray())
    val rs = stm.executeQuery()
    if (!rs.next()) throw NotFoundException(notFoundMessage())
    val cols = columns + extractColumnsFromSelectStatement(statement)
    val results = rs.get(*cols.toTypedArray())
    stm.close()
    rs.close()
    return results
}

fun Connection.executeQueries(statement: String, args: List<Any?> = emptyList(), columns: List<String> = emptyList()): List<List<String>> {
    val stm = prepareStatement(statement)
    stm.set(*args.toTypedArray())
    val rs = stm.executeQuery()
    val cols = columns + extractColumnsFromSelectStatement(statement)
    val results = mutableListOf<List<String>>()
    while (rs.next()) {
        val values = rs.get(*cols.toTypedArray())
        results.add(values)
    }
    stm.close()
    rs.close()
    return results.toList()
}

fun checkIfUserAlreadyExists(source: Source, name: String?, email: String?) {
    if (name != null) {
        source.conn.checkIfAlreadyExists("\"user\"", "name", name) { "User with username $name already exists" }
    }
    if (email != null) {
        source.conn.checkIfAlreadyExists("\"user\"", "email", email) { "User with email $email already exists" }
    }
}

fun <T> Connection.checkIfAlreadyExists(table: String, column: String, value: T, message: () -> String) {
    val stm = prepareStatement("select 1 from $table where $column = ?")
    stm.set(value)
    val rs = stm.executeQuery()
    val alreadyExists = rs.next()
    rs.close()
    stm.close()
    if (alreadyExists) {
        throw AlreadyExistsException(message())
    }
}

fun <T> Connection.checkIfAlreadyExists(table: String, col1: String, val1: T, col2: String, val2: T, message: () -> String) {
    val stm = prepareStatement("select 1 from $table where $col1 = ? and $col2 = ?")
    stm.set(val1, val2)
    val rs = stm.executeQuery()
    val alreadyExists = rs.next()
    rs.close()
    stm.close()
    if (alreadyExists) {
        throw AlreadyExistsException(message())
    }
}

fun <T> Connection.checkIfNotExists(table: String, column: String, value: T, message: () -> String) {
    val stm = prepareStatement("select 1 from $table where $column = ?")
    stm.set(value)
    val rs = stm.executeQuery()
    val notExists = !rs.next()
    rs.close()
    stm.close()
    if (notExists) {
        throw NotFoundException(message())
    }
}

private fun Connection.getListDatabaseIndices(boardId: Int): List<Int> {
    val stm = "select index from list where boardId = ? order by index"
    val args = listOf(boardId)
    return executeQueries(stm, args).map { it[0].toInt() }
}

private fun Connection.getCardDatabaseIndices(listId: Int): List<Int> {
    val stm = "select index from card where listId = ? order by index"
    val args = listOf(listId)
    return executeQueries(stm, args).map { it[0].toInt() }
}

fun Connection.getListClientIndex(dbIndex: Int, boardId: Int): Int {
    return getListDatabaseIndices(boardId).indexOf(dbIndex)
}

fun Connection.getCardClientIndex(dbIndex: Int, listId: Int): Int {
    return getCardDatabaseIndices(listId).indexOf(dbIndex)
}

fun Connection.getNewListIndex(destIndex: Int, listId: Int): Int {
    val stm = "select index, boardId from list where id = ?"
    val args = listOf(listId)
    val (srcIndex, boardId) = executeQuery(stm, args) { "List with id $listId not found" }.map { it.toInt() }
    return getNewIndex("list", "boardId", boardId, srcIndex, destIndex)
}

fun Connection.getNewCardIndex(destIndex: Int, cardId: Int, destListId: Int): Int {
    val stm = "select index, listId from card where id = ?"
    val args = listOf(cardId)
    val (srcIndex, srcListId) = executeQuery(stm, args) { "Card with id $cardId not found" }.map { it.toInt() }

    if (srcListId != destListId) {
        val stmLast = "select max(index) as maxIdx from card where listId = ?"
        val argsLast = listOf(destListId)
        val columns = listOf("maxIdx")
        val (maxIdx) = executeQuery(stmLast, argsLast, columns)

        val newSrcIndex = (maxIdx?.toInt() ?: 0) + INDEX_GAP
        val updateStm = "update card set listId = ?, index = ? where id = ?"
        val updateArgs = listOf(destListId, newSrcIndex, cardId)
        execute(updateStm, updateArgs)
        return getNewIndex("card", "listId", destListId, newSrcIndex, destIndex)
    }
    return getNewIndex("card", "listId", destListId, srcIndex, destIndex)
}

private fun Connection.getNewIndex(table: String, parentName: String, parentId: Int, srcIdx: Int, destIdx: Int): Int {
    val dbIndices = if (table == "list") getListDatabaseIndices(parentId) else getCardDatabaseIndices(parentId)
    val destDbIndex = dbIndices.getOrNull(destIdx) ?: INDEX_GAP
    val destIndex = getAverageIndex(dbIndices, srcIdx, destDbIndex)
    val collision = checkIndexCollision(table, parentName, parentId, destIndex)
    return if (collision) getNewIndex(table, parentName, parentId, srcIdx, destIdx) else destIndex
}

private fun getAverageIndex(dbIndices: List<Int>, srcIndex: Int, destIndex: Int): Int {
    val srcClientIndex = dbIndices.indexOf(srcIndex)
    val destClientIndex = dbIndices.indexOf(destIndex)
    val offset = if (destClientIndex > srcClientIndex) 1 else -1
    val nextDbIndexValue = dbIndices.getOrNull(destClientIndex + offset) ?: (destIndex + INDEX_GAP * offset)
    return (destIndex + nextDbIndexValue) / 2
}

private fun Connection.checkIndexCollision(table: String, parentName: String, parentId: Int, index: Int): Boolean {
    val stm = prepareStatement("select 1 from $table where $parentName = ? and index = ?")
    stm.set(parentId, index)
    val rs = stm.executeQuery()
    val collision = rs.next()
    if (collision) {
        resetIndices(table, parentName, parentId)
        return true
    }
    return false
}

fun Connection.resetIndices(table: String, parentName: String, parentId: Int) {
    val stm = """
        update $table
        set index = normalizedIndices.normalizedIdx * ?
        from (
            select id, row_number() over (order by index) AS normalizedIdx
            from $table
            where $parentName = ?
        ) as normalizedIndices
        where normalizedIndices.id = $table.id
    """.trimIndent()

    val args = listOf(INDEX_GAP, parentId)
    execute(stm, args)
}

fun Connection.getNextIndex(value: String, table: String, condition: String = ""): Int {
    return getNext(0, value, table, condition)
}

private fun Connection.getNext(init: Int, value: String, table: String, condition: String = ""): Int {
    val stm = prepareStatement("select MAX($value) as value from $table $condition;")
    val rs = stm.executeQuery()
    val max = if (!rs.next()) init else rs.getInt("value") + INDEX_GAP
    rs.close()
    stm.close()
    return max
}

fun Connection.getListBoardId(listId: Int): Int {
    val stm = "select boardId from list where id = ?"
    val args = listOf(listId)
    val (boardId) = executeQuery(stm, args) { "List with id $listId not found" }
    return boardId.toInt()
}

fun <T> PreparedStatement.set(vararg values: T) {
    values.forEachIndexed { index, value -> setValue(index + 1, value) }
}

private fun <T : Any> PreparedStatement.setValue(pos: Int, value: T?) {
    when (value) {
        null -> setNull(pos, Types.NULL)
        is String -> setString(pos, value)
        is Int -> setInt(pos, value)
        is Boolean -> setBoolean(pos, value)
        is LocalDateTime -> setTimestamp(pos, value.toTimestamp())
        else -> throw IllegalArgumentException("Type not supported: ${value::class.simpleName}")
    }
}

fun ResultSet.get(vararg values: String): List<String> {
    return values.map { getString(it) }
}

fun getValuesToUpdate(vararg values: Pair<String, Any?>): Pair<String, Array<Any>> {
    val valuesMap = values.mapNotNull { (key, value) -> value?.let { key to it } }.toMap()
    require(valuesMap.isNotEmpty()) { "No values to update" }
    return valuesMap.entries.joinToString(", ") { "${it.key} = ?" } to valuesMap.values.toTypedArray()
}

fun extractColumnsFromSelectStatement(stm: String): List<String> {
    val regex = Regex("""select\s+(.+)\s+from""", RegexOption.IGNORE_CASE)
    val matchResult = regex.find(stm) ?: return emptyList()
    val columnsString = matchResult.groupValues[1].trim()
    return columnsString.split(",").filter { !it.contains("(") && !it.contains(")") }.map { it.trim() }
}

fun Connection.getList(query: List<String>): pt.isel.ls.boardio.domain.lists.List {
    val (id, name, boardId, idx, archived) = query
    val index = getListClientIndex(idx.toInt(), boardId.toInt())
    val createdDate = query[5].toLocalDateTime()
    return pt.isel.ls.boardio.domain.lists.List(id.toInt(), name, boardId.toInt(), index, archived == "t", createdDate)
}

fun Connection.getCard(query: List<String>): Card {
    val (id, name, description, listId, idx) = query
    val index = getCardClientIndex(idx.toInt(), listId.toInt())
    val dueDate = query[5]?.toLocalDateTime() // nullable
    val archived = query[6] == "t"
    val createdDate = query[7].toLocalDateTime()
    return Card(id.toInt(), name, description, listId.toInt(), index, dueDate, archived, createdDate)
}
