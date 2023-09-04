import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../../utils/dom-elements.js"
import requests from "../../api-requests.js"
import { renderAddBoard, renderBoardDetails, renderBoardPreview, renderUpdateBoardPopup } from "./board-views.js"
import { closePopup, popupButton } from "../../views/popup.js"
import { renderDropdown } from "../../views/dropdown.js"
import { getFormValuesById } from "../../utils/dom-utils.js"


export const createBoard = async () => {
    const values = getFormValuesById("add-board")
    const name = values.name
    const description = values.description || "No description"
    const response = await requests.boards.createBoard(name, description)
    const board = {
        id: response.id,
        name,
        description
    }

    const boardPreview = renderBoardPreview(board)
    document.getElementById("add-board").remove()
    document.getElementById("boards-container").appendChildren(
        boardPreview,
        renderAddBoard()
    )
    document.getElementById("user-boards").appendChild(
        a({ href: "#boards/" + board.id, id: "board-preview-" + board.id },
            p({ class: "board-name" }, board.name),
        )
    )
    addBoardDropdownEventListener(board, boardPreview)
    document.getElementById("add-board-button").click()
}

export const updateBoard = async (board) => {
    const values = getFormValuesById("update-board-form")
    const name = values.name || null
    const description = values.description || null
    await requests.boards.updateBoard(board.id, { name, description })
    board.name = values.name || board.name
    board.description = values.description || board.description

    const boardDiv = document.getElementById("board-" + board.id)
    boardDiv.querySelector(".board-name").textContent = board.name
    boardDiv.querySelector(".board-description").textContent = board.description
    document.getElementById("sidebar-board-" + board.id).textContent = board.name
    closePopup()
}

export const deleteBoard = async (boardId) => {
    await requests.boards.deleteBoard(boardId)
    removeBoard(boardId)
    location.hash = "home"
}

export const removeBoard = (boardId) => {
    document.getElementById("board-preview-" + boardId)?.remove()
    document.getElementById("sidebar-board-" + boardId)?.parentElement.remove()
}

export const addBoardDropdownEventListener = (board, boardDiv) =>
    renderDropdown(boardDiv, "contextmenu", null,
        popupButton({}, () => renderBoardDetails(board), icon("tag"), "Board Details"),
        popupButton({}, () => renderUpdateBoardPopup(board), icon("pencil"), "Edit Board"),
        button({ class: "delete-btn" }, async () => { await deleteBoard(board.id); boardDiv.remove() },
            icon("trash-o"), "Delete Board"
        )
    )

export const getArchivedItems = async (boardId) => {
    const lists = await requests.boards.getBoardLists(boardId)
    const archivedLists = lists.filterArchived()
    const archivedCards = await Promise.all(
        lists.map(async (list) => (await requests.lists.getListCards(list.id)).filterArchived())
    )
    return { lists: archivedLists, cards: archivedCards.flat() }
}


export const addManageUsersDropdownEventListener = (input) => {
    let users
    input.addEventListener("focus", async (e) => {
        e.preventDefault()
        users = await requests.users.getUsers()
    })

    input.addEventListener("keyup", (e) => {
        e.preventDefault()

        if(input.value === "") {
            const dropdown = document.querySelector(".manage-users-dropdown")?.parentElement
            if(dropdown) dropdown.remove()
            return
        }
        const matches = []
        users.forEach(user => {
            if (user.name.toLowerCase().includes(input.value.toLowerCase())) {
                matches.push(user.name)
            }
        })

        const results = matches.every(match => match.length === 0)
            ? [ li({ class: "dropdown-item" }, "No results found") ]
            : matches.map(username => li({ class: "dropdown-item" }, button({}, () => {
                input.value = username
                const dropdown = document.querySelector(".dropdown")
                if(dropdown) dropdown.remove()
            }, username)))

        const pos = input.getBoundingClientRect()
        const dropdown = div({ class: "dropdown", id:"manage-users-dropdown", style: `top:${pos.y}px; left: ${pos.x}px;` },
            ul({ class: "manage-users-dropdown dropdown-content" },
                ...results
            )
        )
        const mainContent = document.getElementById("main-content")
        const prevDropdown = document.querySelector(".dropdown")
        prevDropdown ? prevDropdown.replaceWith(dropdown) : mainContent.appendChildren(dropdown)

        document.addEventListener("mousedown", (e) => {
            if (e.target !== input && !e.target.closest(".dropdown")) {
                dropdown.remove()
            }
        })
    })

    return input
}
