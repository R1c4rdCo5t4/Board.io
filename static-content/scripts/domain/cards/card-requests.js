import { fetchAPI, require, getToken } from "../../api-requests.js"


async function createCard(name, description, listId, dueDate) {
    require(name, "Card name is required")
    require(listId, "List id is required")
    return await fetchAPI(`/cards`, {
        method: "POST",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify({
            name,
            description,
            listId,
            dueDate
        })
    })
}

async function getCard(cardId) {
    require(cardId, "Card id is required")
    return await fetchAPI(`/cards/${cardId}`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function moveCard(cardId, listId, index) {
    require(cardId, "Card id is required")
    require(listId, "List id is required")
    require(index, "Card index is required")
    return await fetchAPI(`/cards/${cardId}/move?listId=${listId}&index=${index}`, {
        method: "PATCH",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function deleteCard(cardId) {
    require(cardId, "Card id is required")
    return await fetchAPI(`/cards/${cardId}`, {
        method: "DELETE",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function updateCard(cardId, values) {
    require(cardId, "Card id is required")
    return await fetchAPI(`/cards/${cardId}`, {
        method: "PATCH",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify(values.filterNotNullProperties())
    })
}

async function updateCardDueDate(cardId, dueDate) {
    require(cardId, "Card id is required")
    require(dueDate, "Due date is required")
    return await fetchAPI(`/cards/${cardId}/duedate`, {
        method: "PATCH",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify({ dueDate })
    })
}

export default {
    createCard,
    getCard,
    moveCard,
    deleteCard,
    updateCard,
    updateCardDueDate
}