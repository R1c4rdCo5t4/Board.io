import { fetchAPI, require, getToken } from "../../api-requests.js"


async function createBoard(name, description) {
    require(name, "Board name is required")
    require(description, "Board description is required")
    return await fetchAPI(`/boards`, {
        method: "POST",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify({ name, description })
    })
}

async function getBoard(boardId) {
    require(boardId, "Board id is required")
    return await fetchAPI(`/boards/${boardId}`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function getUserBoards(usertoken) {
    return await fetchAPI(`/boards`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + (usertoken || getToken())
        }
    })
}

async function getBoardLists(boardId) {
    require(boardId, "Board id is required")
    return await fetchAPI(`/boards/${boardId}/lists`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function getBoardUsers(boardId) {
    require(boardId, "Board id is required")
    return await fetchAPI(`/boards/${boardId}/users`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function addUserToBoard(userId, boardId) {
    require(userId, "User id is required")
    require(boardId, "Board id is required")
    return await fetchAPI(`/boards/${boardId}/add?userId=${userId}`, {
        method: "PUT",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function removeUserFromBoard(userId, boardId) {
    require(userId, "User id is required")
    require(boardId, "Board id is required")
    return await fetchAPI(`/boards/${boardId}/remove?userId=${userId}`, {
        method: "PUT",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

async function updateBoard(boardId, values) {
    require(boardId, "Board id is required")
    return await fetchAPI(`/boards/${boardId}`, {
        method: "PATCH",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify(values.filterNotNullProperties())
    })
}

async function deleteBoard(boardId) {
    require(boardId, "Board id is required")
    return await fetchAPI(`/boards/${boardId}`, {
        method: "DELETE",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

export default {
    createBoard,
    getBoard,
    getUserBoards,
    getBoardLists,
    getBoardUsers,
    addUserToBoard,
    updateBoard,
    deleteBoard,
    removeUserFromBoard
}