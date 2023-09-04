import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../../utils/dom-elements.js"
import { renderList, renderAddListButton } from "../lists/list-views.js"
import { popupButton } from "../../views/popup.js"
import { renderManageUsersPopup } from "../users/user-views.js"
import { renderDropdown } from "../../views/dropdown.js"
import { addBoardEventListener, unarchiveList, deleteArchivedList } from "../lists/list-operations.js"
import { createBoard, updateBoard, deleteBoard, getArchivedItems } from "./board-operations.js"
import { MAX_DESCRIPTION_SIZE, MAX_NAME_SIZE } from "../../utils/constants.js"
import { unarchiveCard, deleteArchivedCard } from "../cards/card-operations.js"
import { listItem } from "../../utils/dom-utils.js"


export const renderBoard = (board, lists, cards, boardUsers) => {
    const boardDiv = div({ id: "main-page", class: "board-content" },
        renderBoardMenu(board, boardUsers),
        div({ id: "board-container" },
            ...lists.map((list, index) => renderList(list, cards[index])),
            renderAddListButton(board.id)
        ),
        div({ id: "next-prev-boards"},
            a({ href: "#boards/" + board.prevBoardId }, board.prevBoardId ? icon("arrow-left") : ""),
            a({ href: "#boards/" + board.nextBoardId }, board.nextBoardId ? icon("arrow-right") : "")
        )
    )
    addBoardEventListener(boardDiv)
    return boardDiv
}

export const renderBoardMenu = (board, boardUsers) =>
    div({ class: "menu-bar" },
        div({ class: "menu-bar-content", id: "board-" + board.id },
            div({ class: "menu-bar-top" },
                popupButton({ class: "board-details-popup-btn" }, () => renderBoardDetails(board), h2({ class: "board-name" }, board.name)),
                button({ class: "fa fa-ellipsis-h" }).also((button) => addBoardDropdownEventListener(board, button, renderAddListButton(board.id)))
            ),
            div({ class: "menu-bar-bottom" },
                icon("circle"),
                p({ class: "board-description" }, board.description),
            )
        ),
        popupButton({}, () => renderManageUsersPopup(board.id, boardUsers), "Manage Users")
    )


export const renderAddBoardButton = () =>
    button({ class: "add-board-button", id: "add-board-button" }, async () => {
        document.getElementById("add-board").replaceWith(renderAddBoardForm())
        document.getElementById("add-board-name").focus() },
        icon("light", "plus"), "Add Board"
    )

export const renderAddBoardForm = () =>
    form({ class: "add-board form-container", id: "add-board" },
        input({ type: "text", id: "add-board-name", name: "name", maxlength: MAX_NAME_SIZE, placeholder: "Board Name..." }),
        input({ type: "text", id: "add-board-description", name: "description", maxlength: MAX_DESCRIPTION_SIZE, placeholder: "Board Description..." }),
        div({ class: "buttons-container" },
            button({ id: "add-board-button", class: "confirm" }, async () => await createBoard(), "Add Board"),
            button({ id: "cancel-board-button", class: "fa fa-times" }, () => {
                document.getElementById("add-board").replaceWith(renderAddBoard())
            })
        )
    )

export const renderBoardPreview = (board) =>
    div({ class: "board-preview" },
        a({ href: "#boards/" + board.id },
            div({ class: "list" },
                div({ class: "list-border" }),
                div({ class: "list-content", id: "board-" + board.id },
                    h3({ class: "board-name" }, board.name),
                    p({ class: "board-description" }, board.description),
                )
            )
        )
    )

export const renderAddBoard = () =>
    div({ id: "add-board" },
        div({ class: "list" },
            div({ class: "list-border" }),
            div({ class: "list-content" },
                renderAddBoardButton()
            )
        )
    )

const addBoardDropdownEventListener = (board, clickable, addListButton) =>
    renderDropdown(clickable, "click", null,
        popupButton({}, () => renderBoardDetails(board), icon("tag"), "Board Details"),
        button({}, async () => await addListButton.click(), icon("plus"), "Add List"),
        popupButton({}, () => renderUpdateBoardPopup(board), icon("pencil"), "Edit Board"),
        popupButton({}, async () => renderArchivedItemsPopup(await getArchivedItems(board.id)), icon("archive"), "Archived Items"),
        button({ class: "delete-btn" }, async () => await deleteBoard(board.id), icon("trash-o"), "Delete Board")
    )

export const renderBoardDetails = (board) =>
    div({ class: "popup" },
        div({ class: "popup-content" },
            div({ class: "popup-header" },
                h2({}, "Board Details"),
            ),
            listItem("tag", "Board name:", board.name),
            listItem("align-left", "Board description:", board.description),
            listItem("id-card", "Board id:", board.id),
        )
    )
    
export const renderUpdateBoardPopup = (board) =>
    div({ class: "popup", id: "update-board" },
        div({ class: "popup-content" },
            div({ class: "popup-header" },
                h2({}, "Update Board"),
            ),
            form({ id: "update-board-form", class: "form-container"},
                input({ type: "text", id: "update-board-name", name: "name", maxlength: MAX_NAME_SIZE, placeholder: "New Board Name..." }),
                input({ type: "text", id: "update-board-description", name: "description", maxlength: MAX_DESCRIPTION_SIZE, placeholder: "New Board Description..." }),
                div({ id: "update-list-buttons" },
                    button({ id: "update-list-button", class: "confirm"}, async () => await updateBoard(board), "Update Board"),
                )
            )
        )
    )

export const renderArchivedItemsPopup = (items) =>
    div({ class: "popup" },
        div({ class: "popup-content" },
            div({ class: "popup-header" },
                h2({}, "Archived Items"),
            ),
            items.lists.length > 0 ?
                div({},
                    h3({ id: "archived-lists-title" }, "Archived Lists:"),
                    div({ id: "archived-lists"},
                        ...items.lists.map(list =>
                            div({ class: "archived-item", id: "archived-list-" + list.id },
                                h4({}, list.name),
                                div({},
                                    button({ class: "delete-btn fa fa-trash-o" }, async () => await deleteArchivedList(list.id)),
                                    button({ class: "fa fa-undo" }, async () => {
                                        await unarchiveList(list, items.lists.length)
                                        if(list.archivedAmount - 1 === 0) document.getElementById("archived-lists-title").textContent = "No Archived Lists"
                                    }),
                                )
                            )
                        )
                    )
                )
            : h3({}, "No Archived Lists"),

            items.cards.length > 0 ?
                div({},
                    h3({ id: "archived-cards-title" }, "Archived Cards:"),
                    div({ id: "archived-cards"},
                        ...items.cards.map(card =>
                            div({ class: "archived-item", id: "archived-card-" + card.id },
                                h4({}, card.name),
                                div({},
                                    button({ class: "delete-btn fa fa-trash-o" }, async () => await deleteArchivedCard(card.id)),
                                    button({ class: "fa fa-undo" }, async () => {
                                        await unarchiveCard(card)
                                        if(items.cards.length - 1 === 0) document.getElementById("archived-cards-title").textContent = "No Archived Cards"
                                    }),
                                )
                            )
                        )
                    )
                )
            : h3({}, "No Archived Cards"),
        )
    )

    