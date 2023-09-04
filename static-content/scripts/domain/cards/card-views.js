import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../../utils/dom-elements.js"
import { renderDropdown } from "../../views/dropdown.js"
import { popupButton } from "../../views/popup.js"
import { createCard, updateCard, deleteCard, addCardEventListener, updateCardDueDate, archiveCard, duplicateCard } from "./card-operations.js"
import { MAX_DESCRIPTION_SIZE, MAX_NAME_SIZE } from "../../utils/constants.js"
import { dateIsOverdue, formatFullDate, formatSimpleDate, getCurrentTime, getNextDayDate } from "../../utils/date.js"
import {listItem} from "../../utils/dom-utils.js"


export const renderCard = (card) =>
    div({ class: "card", id: "card-" + card.id, draggable: "true" },
        popupButton({ class: "card-details-button", id: "card-details-" + card.id }, () => renderCardDetails(card),
            div({ class: "card-header" }, p({ id: "card-name" + card.id }, card.name))
        ),
        renderCardDueDate(card.dueDate),
    ).also(cardDiv => {
        addCardEventListener(card, cardDiv)
        addCardDropdownEventListener(card, cardDiv)
    })


export const renderCardDueDate = (dueDate) =>
    small({ class: dateIsOverdue(dueDate) ? "overdue duedate" : "duedate"}, formatSimpleDate(dueDate))


export const renderAddCardButton = (listId) => 
    button({ class: "add-card-button", id: "add-card-button-" + listId }, () => {
        const prevDiv = document.getElementById("add-card")
        const prevListId = prevDiv?.parentElement?.id?.replace("add-card-", "")
        const prevAddCardDiv = document.getElementById("add-card-" + prevListId)
        if (prevAddCardDiv) {
            prevAddCardDiv.replaceChildren(
                renderAddCardButton(prevListId)
            )
        }
        document.getElementById("add-card-" + listId).replaceChildren(renderAddCardForm(listId))
        document.getElementById("add-card-title").focus()
        document.getElementById("add-card-button").scrollIntoView({ behavior: 'smooth' })
    }, icon("light", "plus"), "Add card")


export const renderAddCardForm = (listId) =>
    form({ class: "add-card form-container", id: "add-card" },
        input({ type: "text", id: "add-card-title", name:"name", maxlength: MAX_NAME_SIZE, placeholder: "Card Title..." }),
        div({ class: "buttons-container" },
            button({ id: "add-card-button", class: "confirm" }, async () => await createCard(listId), "Add Card"),
            button({ id: "cancel-card-button", class: "fa fa-times" }, () => {
                document.getElementById("add-card-" + listId).replaceChildren(
                    renderAddCardButton(listId)
                )
            })
        )
    )

export const renderCardDetails = (card) =>
    div({ class: "popup" },
        div({ class: "popup-content" },
            div({ class: "popup-header" },
                h2({}, "Card Details"),
            ),
            listItem("tag", "Card name:", card.name),
            listItem("align-left", "Description:", card.description),
            listItem("calendar-o", "Due date:", formatFullDate(card.dueDate) || "None"),
            listItem("calendar", "Created at:", formatFullDate(card.createdDate)),
            listItem("id-card", "Card id:", card.id),
            listItem("id-card-o", "List id:", card.listId),
        )
    )

export const addCardDropdownEventListener = (card, clickable) =>
    renderDropdown(clickable, "contextmenu", null,
        popupButton({}, () => renderCardDetails(card), icon("info-circle"), "Card Details"),
        popupButton({}, () => renderUpdateCardPopup(card), icon("pencil"), "Edit Card"),
        popupButton({}, () => renderDueDatePopup(card), icon("calendar-o"), "Due Date"),
        button({}, async () => await duplicateCard(card), icon("clone"), "Duplicate Card"),
        button({}, async () => await archiveCard(card), icon("archive"), "Archive Card"),
        button({ class: "delete-btn" }, async () => await deleteCard(card), icon("trash-o"), "Delete Card"),
    )


const renderUpdateCardPopup = (card) =>
    div({ class: "popup", id: "update-card" },
        div({ class: "popup-content" },
            div({ class: "popup-header" },
                h2({}, "Update Card"),
            ),
            form({ class: "update-card form-container", id: "update-card-form" },
                input({ type: "text", id: "update-card-title", name: "name", maxlength: MAX_NAME_SIZE, placeholder: "New Card Name..." }),
                textarea({ type: "text", id: "update-card-description", name: "description", maxlength: MAX_DESCRIPTION_SIZE, placeholder: "New Card Description..." }),
                div({ id: "update-card-buttons" },
                    button({ id: "update-card-button", class: "confirm" }, async () => await updateCard(card), "Update Card"),
                )
            )
        )
    )

const renderDueDatePopup = (card) =>
    div({ class: "popup", id: "due-date-card" },
        div({ class: "popup-content" },
            div({ class: "popup-header" },
                h2({}, "Set Card Due Date"),
            ),
            div({ class: "duedate" },
                form({ class: "form-container" },
                    label({ for: "update-card-due-date" }, "Due Date: "),
                    input({ type: "date", id: "update-card-due-date", value: card.dueDate?.substring(0,10) || getNextDayDate() }),
                    label({ for: "update-card-due-time" }, "Due Time: "),
                    input({ type: "time", id: "update-card-due-time", value: card.dueDate?.substring(11) || getCurrentTime() }),
                    div({ id: "update-card-buttons" },
                        button({ id: "update-card-button", class: "confirm" }, async () => await updateCardDueDate(card), "Update"),
                        button({ id: "remove-due-date-button" }, async () => await updateCardDueDate(card, true), "Remove"),
                    )
                )
            )
        )
    )
