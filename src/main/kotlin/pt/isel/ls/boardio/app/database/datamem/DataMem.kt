package pt.isel.ls.boardio.app.database.datamem

import pt.isel.ls.boardio.domain.boards.Board
import pt.isel.ls.boardio.domain.boards.UserBoard
import pt.isel.ls.boardio.domain.cards.Card
import pt.isel.ls.boardio.domain.lists.List
import pt.isel.ls.boardio.domain.users.User
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class DataMem {
    val users = ConcurrentHashMap<Int, User>()
    val boards = ConcurrentHashMap<Int, Board>()
    val lists = ConcurrentHashMap<Int, List>()
    val cards = ConcurrentHashMap<Int, Card>()
    val userBoards = CopyOnWriteArrayList<UserBoard>()
    val domain get() = listOf(users, boards, lists, cards)
}
