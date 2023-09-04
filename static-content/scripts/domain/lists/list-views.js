import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, icon } from "../../utils/dom-elements.js"
import { renderCard, renderAddCardButton } from "../cards/card-views.js"
import { addListEventListener } from "../cards/card-operations.js"
import { popupButton } from "../../views/popup.js"
import { renderDropdown } from "../../views/dropdown.js"
import {createList, deleteList, updateList, addMoveListEventListener, archiveList} from "./list-operations.js"
import { MAX_NAME_SIZE } from "../../utils/constants.js"
import {listItem} from "../../utils/dom-utils.js"


export const renderList = (list, cards) => {
    const dropdownButton = button({ class: "fa fa-ellipsis-h" })
    const addCardButton = renderAddCardButton(list.id)
    const listDiv = div({ class: "list", id: "list-" + list.id, draggable: "true" },
        div({ class: "list-border" }),
        div({ class: "list-content", id: "list-content-" + list.id },
            div({ class: "list-header" },
                popupButton({ class: "list-details-button", id: "list-details-" + list.id }, () => renderListDetails(list),
                    div({class: "list-title"},
                    h3({ id: "list-name-" + list.id }, list.name),
                    small({id: "list-count-" + list.id}, cards?.length || "0"),
                )),
                dropdownButton
            ),
            div({ class: "cards-container", id: "cards-container-" + list.id },
                ...cards?.map((card) => renderCard(card)),
                div({ id: "add-card-" + list.id },
                    addCardButton
                )
            )
        )
    ).also(listDiv => {
        addListEventListener(listDiv)
        addMoveListEventListener(list, listDiv)
    })
    renderListDropdown(list, cards, dropdownButton, addCardButton)
    return listDiv
}


export const renderAddListButton = (boardId) => 
    button({ class: "add-list-button", id: "add-list" }, async () => {
        document.getElementById("add-list").replaceWith(renderAddListForm(boardId))
        document.getElementById("add-list-title").focus()
    }, icon("light", "plus"), "Add List")


export const renderAddListForm = (boardId) =>
    form({ class: "add-list form-container", id: "add-list" },
        input({ type: "text", id: "add-list-title", name: "name", maxlength: MAX_NAME_SIZE, placeholder: "List Name..." }),
        div({ class: "buttons-container" },
            button({ id: "add-list-button", class: "confirm"}, async () => await createList(boardId), "Add List"),
            button({ id: "cancel-list-button", class: "fa fa-times" }, () => {
                document.getElementById("add-list").replaceChildren(
                    renderAddListButton(boardId)
                )
            })
        )
    )

export const renderListDropdown = (list, cards, clickable, addCardButton) =>
    renderDropdown(clickable, "click", null,
        popupButton({}, () => renderListDetails(list), icon("info-circle"), "List Details"),
        button({}, () => addCardButton.click(), icon("plus"), "Add Card"),
        popupButton({}, () => renderUpdateListPopup(list, cards), icon("pencil"), "Edit List"),
        button({}, async () => await archiveList(list), icon("archive"), "Archive List"),
        button({ class: "delete-btn" }, async () => await deleteList(list), icon("trash-o"), "Delete List"),
    )

export const renderListDetails = (list) =>
    div({ class: "popup" },
        div({ class: "popup-content" },
            div({ class: "popup-header" },
                h2({}, "List Details"),
            ),
            listItem("tag", "List name:", list.name),
            listItem("id-card", "List id:", list.id),
            listItem("id-card-o", "Board id: ", list.boardId),
        )
    )

const renderUpdateListPopup = (list, cards) =>
    div({ class: "popup", id: "update-list" },
        div({ class: "popup-content" },
            div({ class: "popup-header" },
                h2({}, "Update List"),
            ),
            form({ class: "update-list form-container", id: "update-list-form"},
                input({ type: "text", id: "update-list-title", name: "name", placeholder: "New List Name..." }),
                div({ id: "update-list-buttons" },
                    button({ id: "update-list-button", class: "confirm"}, async () => await updateList(list, cards), "Update List"),
                )
            )
        )
    )
