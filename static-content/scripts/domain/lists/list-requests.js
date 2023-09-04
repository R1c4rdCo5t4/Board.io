import { fetchAPI, getToken, require } from "../../api-requests.js"


async function createList(name, boardId) {
    require(name, "List name is required")
    require(boardId, "Board id is required")
    return await fetchAPI(`/lists`, {
        method: "POST",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify({ name, boardId })
    })
}

async function getList(listId) {
    require(listId, "List id is required: " + listId)
    return await fetchAPI(`/lists/${listId}`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function getListCards(listId) {
    require(listId, "List id is required")
    return await fetchAPI(`/lists/${listId}/cards`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function moveList(listId, index) {
    require(listId, "List id is required")
    require(index, "List index is required")
    return await fetchAPI(`/lists/${listId}/move?index=${index}`, {
        method: "PATCH",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function deleteList(listId) {
    require(listId, "List id is required")
    return await fetchAPI(`/lists/${listId}`, {
        method: "DELETE",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function updateList(listId, values) {
    require(listId, "List id is required")
    return await fetchAPI(`/lists/${listId}`, {
        method: "PATCH",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify(values.filterNotNullProperties())
    })
}

export default {
    createList,
    getList,
    getListCards,
    moveList,
    deleteList,
    updateList
}