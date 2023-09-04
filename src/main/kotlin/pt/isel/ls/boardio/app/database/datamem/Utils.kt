package pt.isel.ls.boardio.app.database.datamem

import pt.isel.ls.boardio.app.utils.AlreadyExistsException
import pt.isel.ls.boardio.domain.Domain
import java.util.Collections
import kotlin.reflect.KProperty1

fun Collection<Int>.getNextId(): Int = getNext(1)

fun Collection<Int>.getNextIndex(): Int = getNext(0)

private fun Collection<Int>.getNext(init: Int): Int = if (isEmpty()) init else Collections.max(this) + 1

fun DataMem.updateCardIndices(cardId: Int, listId: Int) {
    val card = cards[cardId]
    val list = lists[listId]
    requireNotNull(card) { "Card with id $cardId was not found" }
    requireNotNull(list) { "List with id $listId was not found" }

    val cardsToUpdate = cards.values.filter { it.listId == listId && it.index > card.index }
    cardsToUpdate.forEach { cards[it.id] = it.copy(index = it.index - 1) }
}

fun DataMem.updateListIndices(listId: Int, boardId: Int, prevIndex: Int, index: Int) {
    val list = lists[listId]
    val board = boards[boardId]
    requireNotNull(list) { "List with id $listId was not found" }
    requireNotNull(board) { "Board with id $boardId was not found" }

    val listsToUpdate = lists.values
        .filter { it.boardId >= boardId && it.id != listId && it.index > prevIndex && it.index <= index }
    listsToUpdate.forEach { lists[it.id] = it.copy(index = it.index - 1) }
}

inline fun <reified T : Domain> checkIfAlreadyExists(domain: Map<Int, T>, fieldValue: Any, prop: KProperty1<T, Any>) {
    val fieldValues = domain.values.map { prop.get(it) }
    if (fieldValues.contains(fieldValue)) {
        throw AlreadyExistsException("${T::class.simpleName} with ${prop.name} $fieldValue already exists")
    }
}
