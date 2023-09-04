import requests from "../../api-requests.js"
import { renderList, renderAddListButton } from "./list-views.js"
import { closePopup } from "../../views/popup.js"
import { totalListCards } from "../cards/card-operations.js"
import {getFormValuesById, removeDraggingShadow, updateElementValueById} from "../../utils/dom-utils.js"


let movedList = {}

const appendList = (list, boardId) => {
    document.getElementById("add-list").replaceWith(
        renderList(list, list.cards || []),
        renderAddListButton(boardId)
    )
    updateElementValueById("board-lists-amount", 1)
    updateElementValueById("board-cards-amount", totalListCards(list.id))
}

export const createList = async (boardId) => {
    const values = getFormValuesById("add-list")
    const response = await requests.lists.createList(values.name, boardId)
    const list = {
        id: response.id,
        name: values.name,
        boardId: boardId,
        index: getMaxListIndex() + 1,
        archived: false
    }
    appendList(list, boardId)
    document.getElementById("add-list").click()
}

export const updateList = async (list, cards) => {
    const values = getFormValuesById("update-list-form")
    await requests.lists.updateList(list.id, { name: values.name })
    list.name = values.name || list.name
    document.getElementById("list-name-" + list.id).textContent = list.name
    closePopup()
}

export const deleteList = async (list) => {
    const cards = totalListCards(list.id)
    await requests.lists.deleteList(list.id)
    document.getElementById("list-" + list.id).remove()
    updateElementValueById("board-lists-amount", -1)
    updateElementValueById("board-cards-amount", -cards)
}

export const addMoveListEventListener = (list, listDiv) => {

    listDiv.addEventListener("dragstart", (e) => {
        if(e.target !== listDiv) return
        listDiv.classList.add("dragging")
        removeDraggingShadow(e)
    })

    listDiv.addEventListener("dragend", async () => {
        listDiv.classList.remove("dragging")
        if (!movedList.id) return

        await requests.lists.moveList(movedList.id, movedList.index)
        movedList = {}
    })
}

export const addBoardEventListener = (board) => {
    board.addEventListener("dragover", e => moveList(e))
    board.addEventListener("dragenter", e => e.preventDefault())
}

const moveList = (e) => {
    e.preventDefault()

    const listsContainer = document.getElementById("board-container")
    const lists = [...listsContainer.querySelectorAll(".list:not(.dragging)")]
    const draggingList = listsContainer.querySelector(".list.dragging")
    const nextList = lists.find(list => {
        const listRect = list.getBoundingClientRect()
        const listMiddle = (listRect.left + listRect.right) / 2
        return e.clientX < listMiddle
    })

    if (draggingList) {
        const referenceNode = nextList || listsContainer.lastElementChild
        listsContainer.insertBefore(draggingList, referenceNode)
        const addList = listsContainer.querySelector("[class^='add-list-']")
        addList.remove()
        listsContainer.appendChild(addList)
        movedList.index = [...listsContainer.children].indexOf(draggingList)
        movedList.id = draggingList.id.replace("list-", "")
    }
}

const getMaxListIndex = () => {
    const lists = [...document.getElementById("board-container").children]
    return lists.length ? Math.max(...lists.map(list => list.index)) : 0
}

export const archiveList = async (list) => {
    await requests.lists.updateList(list.id, { archived: true })
    list.archived = true
    updateElementValueById("board-cards-amount", -totalListCards(list.id))
    updateElementValueById("board-lists-amount", -1)
    document.getElementById("list-" + list.id).remove()
}

export const unarchiveList = async (list) => {
    await requests.lists.updateList(list.id, { archived: false })
    list.cards = (await requests.lists.getListCards(list.id)).filterNotArchived()
    list.archived = false
    document.getElementById("archived-list-" + list.id).remove()
    appendList(list, list.boardId)
}

export const deleteArchivedList = async (listId) => {
    await requests.lists.deleteList(listId)
    document.getElementById("archived-list-" + listId).remove()
}
