package pt.isel.ls.boardio.domain.lists.database

import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.domain.cards.Card
import pt.isel.ls.boardio.domain.lists.List

interface ListsSource {
    fun createList(source: Source, name: String, boardId: Int): Int
    fun getList(source: Source, listId: Int): List
    fun getListCards(source: Source, listId: Int, skip: Int? = null, limit: Int? = null): kotlin.collections.List<Card>
    fun getBoardIdOfList(source: Source, listId: Int): Int
    fun deleteList(source: Source, listId: Int)
    fun moveList(source: Source, listId: Int, index: Int)
    fun updateList(source: Source, listId: Int, name: String?, index: Int?, archived: Boolean?)
}
