import requests from "../api-requests.js"
import { getIdFromHash } from "../utils/hash.js"


export async function listRedirect() {
    const listId = getIdFromHash()
    const list = await requests.lists.getList(listId)
    const board = await requests.boards.getBoard(list.boardId)
    location.hash = `#boards/${board.id}?list=${listId}`
}