import requests from "../api-requests.js"
import { renderBoard } from "../domain/boards/board-views.js"
import { horizontalMouseScroll } from "../views/scroll.js"
import { renderSidebarBoardInfo } from "../views/sidebar.js"
import { renderListDetails } from "../domain/lists/list-views.js"
import { openPopup } from "../views/popup.js"
import { renderCardDetails} from "../domain/cards/card-views.js"
import { getIdFromHash, getQueriesFromHash } from "../utils/hash.js"


export async function boardPage() {
    const boardId = getIdFromHash()
    const board = await requests.boards.getBoard(boardId)
    const boardUsers = await requests.boards.getBoardUsers(boardId)
    const userBoards = await requests.boards.getUserBoards()
    const lists = (await requests.boards.getBoardLists(board.id)).filterNotArchived()
    const cards = (await Promise.all(lists.map(
        async list => (await requests.lists.getListCards(list.id)).filterNotArchived())
    ))

    userBoards.sort((a, b) => a.id - b.id)
    const index = userBoards.findIndex(b => b.id === boardId)
    board.nextBoardId = userBoards[index + 1]?.id
    board.prevBoardId = userBoards[index - 1]?.id

    document.getElementById("main-page").replaceWith(
        renderBoard(board, lists, cards, boardUsers)
    )
    document.getElementById("sidebar-content").appendChildren(
        renderSidebarBoardInfo(boardUsers, lists, cards)
    )
    horizontalMouseScroll()

    const queries = getQueriesFromHash()
    if (queries.list){
        const list = lists.flat().find(list => list.id === parseInt(queries.list))
        if(!list) throw new Error("List not found")
        const popup = renderListDetails(list)
        openPopup(popup)
    }
    else if (queries.card){
        const card = cards.flat().find(card => card.id === parseInt(queries.card))
        if(!card) throw new Error("Card not found")
        const popup = renderCardDetails(card)
        openPopup(popup)
    }
}