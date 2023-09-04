import users from "./domain/users/user-requests.js"
import boards from "./domain/boards/board-requests.js"
import lists from "./domain/lists/list-requests.js"
import cards from "./domain/cards/card-requests.js"
import { alertPopup } from "./views/alert.js"
import { objectToURIQueries } from "./utils/utils.js"

export const API_BASE_URL = "http://localhost:9000/api"

export async function fetchAPI(url, options) {
    try {
        const response = await fetch(API_BASE_URL + url, options)
        const json = await response.json()
        if (!response.ok) {
            throw new Error(json.error)
        }
        return json
    } catch (e) {
        alertPopup(e.message)
        throw e
    }
}

export async function searchAPI(query, skip, limit, options) {
    require(query, "Query is required")
    const queries = options ? objectToURIQueries(options) : ""
    return await fetchAPI(`/search?query=${query}&skip=${skip}&limit=${limit}${queries}`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        }
    })
}

export function require(value, message) {
    if (value === undefined || value === "" || value === false) {
        alertPopup(message)
        throw new Error(message)
    }
}

export function getToken() {
    return localStorage.getItem("token")
}

export function setToken(token) {
    localStorage.setItem("token", token)
}

export function deleteToken() {
    localStorage.removeItem("token")
}

export default {
    users,
    boards,
    lists,
    cards,
    session: {
        getToken,
        setToken,
        deleteToken
    }
}
