import requests from "../../api-requests.js"
import { div } from "../../utils/dom-elements.js"
import { closePopup } from "../../views/popup.js"
import { renderAddCardButton, renderAddCardForm, renderCard, renderCardDueDate } from "./card-views.js"
import {getFormValuesById, removeDraggingShadow, updateElementValueById} from "../../utils/dom-utils.js"

let movedCard = {}

export const createCard = async (listId) => {
    const values = getFormValuesById("add-card")
    const name = values.name
    const description = "No description"
    const response = await requests.cards.createCard(name, description, listId)
    const card = {
        id: response.id,
        name,
        description,
        listId,
        index: getMaxCardIndex(listId) + 1,
        archived: false
    }
    appendCard(card, listId)
    document.getElementById("add-card-button-" + listId).click()
}

export const deleteCard = async (card) => {
    await requests.cards.deleteCard(card.id)
    const cardDiv = document.getElementById("card-" + card.id)
    cardDiv.remove()
    updateElementValueById("list-count-" + card.listId, -1)
    updateElementValueById("board-cards-amount", -1)
}

const appendCard = (card, listId) => {
    document.getElementById("add-card-" + listId)?.remove()
    document.getElementById("cards-container-" + listId)?.appendChildren(
        renderCard(card),
        div({ id: "add-card-" + listId },
            renderAddCardButton(listId)
        )
    )
    updateElementValueById("list-count-" + listId, 1)
    updateElementValueById("board-cards-amount", 1)
}

export const addCardEventListener = (card, cardDiv) => {
    cardDiv.addEventListener("dragstart", (e) => {
        removeDraggingShadow(e)
        cardDiv.classList.add("dragging")
    })

    cardDiv.addEventListener("dragend", async () => {
        cardDiv.classList.remove("dragging")

        const destListId = movedCard.destListId || movedCard.srcListId
        if (!destListId) return

        if(destListId){
            updateElementValueById("list-count-" + destListId, 1)
            updateElementValueById("list-count-" + movedCard.srcListId, -1)
        }
        await requests.cards.moveCard(movedCard.id, destListId, movedCard.index)
        if(destListId) card.listId = destListId
        movedCard = {}
    })
}

export const addListEventListener = (list) => {
    list.addEventListener("dragover", e => moveCard(e))
    list.addEventListener("dragenter", e => e.preventDefault())
}

const moveCard = (e) => {
    e.preventDefault()
  
    const cardsContainer = [...document.querySelectorAll(".cards-container")]
    const draggingItem = document.querySelector(".dragging")
    const currentList = cardsContainer.find(list => list.contains(draggingItem))
    if (!currentList) return
  
    const cards = [...currentList.querySelectorAll(".card:not(.dragging)")]
    const draggingCard = currentList.querySelector(".card.dragging")
    const nextCard = cards.find(card => {
        const cardRect = card.getBoundingClientRect()
        const cardMiddle = (cardRect.bottom + cardRect.top) / 2
        return e.clientY < cardMiddle
    })

    if (draggingCard) {
        const targetList = cardsContainer.find(list => list !== currentList && list.contains(e.target.parentElement))
        if (!targetList) {
            moveCardWithinList(draggingItem, currentList, nextCard)
        } else {
            moveCardToDifferentList(draggingItem, targetList)
        }
        movedCard.id = draggingItem.id.replace("card-", "")
    }
}
  

const moveCardWithinList = (draggingItem, currentList, nextCard) => {
    const referenceNode = nextCard || currentList.lastElementChild
    currentList.insertBefore(draggingItem, referenceNode)

    const addCard = currentList.querySelector("[id^='add-card-']")
    addCard.remove()
    currentList.appendChild(addCard)
    movedCard.index = [...currentList.children].indexOf(draggingItem)

    if (!movedCard.srcListId) movedCard.srcListId = currentList.id.replace("cards-container-", "")
}

const moveCardToDifferentList = (draggingItem, targetList) => {
    draggingItem.remove()
    targetList.insertBefore(draggingItem, targetList.lastChild)
    movedCard.destListId = targetList?.id?.replace("cards-container-", "")
}

export const getMaxCardIndex = (listId) => {
    const cardsContainer = getCardsContainer(listId)
    if(!cardsContainer) return 0
    const cards = Array.from(cardsContainer.querySelectorAll('.card'))
    return cards.length ? Math.max(...cards.map(card => parseInt(card.dataset.index))) : 0
}

export const totalListCards = (listId) => {
    const cards = getCardsContainer(listId)
    return cards ? cards.children.length - 1 : 0
}

const getCardsContainer = (listId) =>
    [...document.querySelectorAll(".cards-container")].find(list => list.id === "cards-container-" + listId)
  
export const updateCard = async (card) => {
    const values = getFormValuesById("update-card-form")
    const name = values.name || null
    const description = values.description || null
    await requests.cards.updateCard(card.id, { name, description })
    card.name = name || card.name
    card.description = description || card.description
    document.getElementById("card-name" + card.id).textContent = card.name
    closePopup()
}

export const updateCardDueDate = async (card, remove = false) => {
    const dueDateDiv = document.getElementById("card-" + card.id).querySelector(".duedate")
    if(remove){
        await requests.cards.updateCardDueDate(card.id, null)
        card.dueDate = null
        dueDateDiv.replaceWith(renderCardDueDate(card.dueDate))
        closePopup()
        return
    }
    const dueDate = document.getElementById("update-card-due-date")?.value
    const dueTime = document.getElementById("update-card-due-time")?.value
    const dueDateFormat = dueDate && dueTime ? `${dueDate}T${dueTime}:00` : card.dueDate
    const dueDateTime = dueDateFormat !== null ? dueDateFormat : undefined

    await requests.cards.updateCardDueDate(card.id, dueDateTime)
    card.dueDate = dueDateFormat || card.dueDate
    dueDateDiv.replaceWith(renderCardDueDate(card.dueDate))
    closePopup()
}

export const duplicateCard = async (card) => {
    const response = await requests.cards.createCard(card.name, card.description, card.listId)
    card = {
        id: response.id,
        name: card.name,
        description: card.description,
        listId: card.listId,
        index: getMaxCardIndex(card.listId) + 1,
        archived: false
    }
    appendCard(card, card.listId)
}

export const archiveCard = async (card) => {
    await requests.cards.updateCard(card.id, { archived: true })
    card.archived = true
    document.getElementById("card-" + card.id).remove()
    updateElementValueById("list-count-" + card.listId, -1)
    updateElementValueById("board-cards-amount", -1)
}

export const unarchiveCard = async (card) => {
    await requests.cards.updateCard(card.id, { archived: false })
    const list = await requests.lists.getList(card.listId)
    card.archived = false
    document.getElementById("archived-card-" + card.id).remove()
    if(list.archived) return
    appendCard(card, card.listId)
}

export const deleteArchivedCard = async (cardId) => {
    await requests.cards.deleteCard(cardId)
    document.getElementById("archived-card-" + cardId).remove()
}