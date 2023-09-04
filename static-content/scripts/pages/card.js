import requests from "../api-requests.js"
import { getIdFromHash } from "../utils/hash.js"


export async function cardRedirect() {
    const cardId = getIdFromHash()
    const card = await requests.cards.getCard(cardId)
    const list = await requests.lists.getList(card.listId)
    const board = await requests.boards.getBoard(list.boardId)
    location.hash = `#boards/${board.id}?card=${cardId}`
}