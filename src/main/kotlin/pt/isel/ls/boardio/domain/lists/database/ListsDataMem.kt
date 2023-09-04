package pt.isel.ls.boardio.domain.lists.database

import pt.isel.ls.boardio.app.database.datamem.checkIfAlreadyExists
import pt.isel.ls.boardio.app.database.datamem.getNextId
import pt.isel.ls.boardio.app.database.datamem.getNextIndex
import pt.isel.ls.boardio.app.database.datamem.updateListIndices
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.app.utils.subSequence
import pt.isel.ls.boardio.domain.cards.Card
import pt.isel.ls.boardio.domain.lists.List

class ListsDataMem : ListsSource {
    override fun createList(source: Source, name: String, boardId: Int): Int {
        checkIfAlreadyExists(source.mem.lists.filter { it.value.boardId == boardId }, name, List::name)
        val listId = source.mem.lists.keys.getNextId()
        val listIndex = source.mem.lists.values.filter { it.boardId == boardId }.map { it.index }.getNextIndex()
        source.mem.lists[listId] = List(listId, name, boardId, listIndex)
        return listId
    }

    override fun getList(source: Source, listId: Int): List {
        return source.mem.lists[listId] ?: throw NotFoundException("List with id $listId not found")
    }

    override fun getListCards(source: Source, listId: Int, skip: Int?, limit: Int?): kotlin.collections.List<Card> {
        if (!source.mem.lists.containsKey(listId)) throw NotFoundException("List with id $listId was not found")
        val cards = source.mem.cards.values.filter { it.listId == listId }
        return cards.subSequence(skip, limit)
    }

    override fun getBoardIdOfList(source: Source, listId: Int): Int {
        return source.mem.lists[listId]?.boardId ?: throw NotFoundException("List with id $listId was not found")
    }

    override fun deleteList(source: Source, listId: Int) {
        if (!source.mem.lists.containsKey(listId)) throw NotFoundException("List with id $listId was not found")
        source.mem.lists.remove(listId)
        source.mem.cards.values.removeAll { it.listId == listId }
    }

    override fun moveList(source: Source, listId: Int, index: Int) {
        val list = source.mem.lists[listId] ?: throw NotFoundException("List with id $listId was not found")
        val boardId = list.boardId
        val oldIdx = list.index
        source.mem.updateListIndices(listId, boardId, oldIdx, index)
        source.mem.lists[listId] = source.mem.lists[listId]?.copy(index = index) ?: return
    }

    override fun updateList(source: Source, listId: Int, name: String?, index: Int?, archived: Boolean?) {
        val list = source.mem.lists[listId] ?: throw NotFoundException("List with id $listId was not found")
        if (name != null) {
            checkIfAlreadyExists(source.mem.lists.filter { it.value.boardId == list.boardId }, name, List::name)
        }

        val prevIdx = list.index
        source.mem.updateListIndices(listId, list.boardId, prevIdx, index ?: prevIdx)
        source.mem.lists[listId] = list.copy(
            name = name ?: list.name,
            index = index ?: prevIdx,
            archived = archived ?: list.archived
        )
    }
}
