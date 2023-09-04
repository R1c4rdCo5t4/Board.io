import { fetchAPI, require, API_BASE_URL, getToken } from "../../api-requests.js"
import { checkPasswordStrength } from "../../utils/utils.js"


async function createUser(name, email, password) {
    require(name, "Username is required")
    require(email, "E-mail is required")
    require(password, "Password is required")
    require(email.includes("@"), "E-mail must be valid")
    checkPasswordStrength(password)
    return await fetchAPI(`/users`, {
        method: "POST",
        headers: {
            "content-type": "application/json",
        },
        body: JSON.stringify({ name, email, password })
    })
}

async function getUser(userId) {
    require(userId, "User id is required")
    return await fetchAPI(`/users/${userId}`, {
        method: "GET",
        headers: {
            "content-type": "application/json",
        }
    })
}

async function getUsers() {
    return await fetchAPI(`/users`)
}

async function loginUser(email, password){
    require(email, "E-mail is required")
    require(password, "Password is required")
    return await fetchAPI(`/users/login`, {
        method: "POST",
        headers: {
            "content-type": "application/json",
        },
        body: JSON.stringify({ email, password })
    })
}

async function getUserByUsername(username) {
    require(username, "Username is required")
    return await fetchAPI(`/users/name/${username}`)
}

async function getUserByToken(token) {
    require(token, "Token is required")
    return await fetch(`${API_BASE_URL}/users/token/${token}`)
}

async function updateUser(password, values) {
    require(password, "Password is required")
    return await fetchAPI(`/users`, {
        method: "PATCH",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify({ password, ...values }.filterNotNullProperties())
    })
}

async function deleteUser(password){
    require(password, "Password is required")
    return await fetchAPI(`/users`, {
        method: "DELETE",
        headers: {
            "content-type": "application/json",
            "authorization": "bearer " + getToken()
        },
        body: JSON.stringify({ password })
    })
}

export default {
    createUser,
    getUser,
    getUsers,
    loginUser,
    getUserByUsername,
    getUserByToken,
    updateUser,
    deleteUser
}