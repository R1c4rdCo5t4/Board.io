import requests, {deleteToken, getToken, require} from "../../api-requests.js"
import { icon } from "../../utils/dom-elements.js"
import { renderBoardUser } from "./user-views.js"
import { closePopup } from "../../views/popup.js"
import { removeBoard } from "../boards/board-operations.js"
import { checkPasswordStrength } from "../../utils/utils.js"
import { renderSidebarBoardUser } from "../../views/sidebar.js"
import { getFormValuesById, updateElementValueById } from "../../utils/dom-utils.js"

let loggedUser = null

export const getLoggedUser = async () => {
    const token = getToken()
    if (loggedUser && loggedUser.token === token) return loggedUser
    const response = await requests.users.getUserByToken(token)
    if (response.status === 401 || response.status === 404) {
        deleteToken()
        window.location.hash = "auth"
        return
    }
    loggedUser = await response.json()
    return loggedUser
}

export const addUserToBoard = async (boardId, username) => {
    const user = await requests.users.getUserByUsername(username)
    if (!user) return

    await requests.boards.addUserToBoard(user.id, boardId)
    document.querySelector(".add-user input").value = ""
    document.querySelector(".board-users").appendChild(renderBoardUser(user, boardId))
    document.getElementById("board-users").appendChild(renderSidebarBoardUser(user))
    updateElementValueById("board-users-amount", 1)
}

export const removeUserFromBoard = async (boardId, userId) => {
    await requests.boards.removeUserFromBoard(userId, boardId)
    document.getElementById("user-" + userId).remove()
    document.getElementById("board-user-" + userId).remove()
    updateElementValueById("board-users-amount", -1)

    const user = await getLoggedUser()
    if(user.id === userId) {
        closePopup()
        removeBoard(boardId)
        window.location.hash = "home"
    }
}

export const updateUser = async () => {
    const values = getFormValuesById("update-user-form")
    const name = values.name || null
    const email = values.email || null
    await requests.users.updateUser(values.password, { name, email })
    if(name) document.getElementById("user-button").replaceChildren(name, icon("user"))
    closePopup()
}

export const changePassword = async () => {
    const values = getFormValuesById("change-password-form")
    require(values.newPassword === values.confirmPassword, "Passwords don't match")
    checkPasswordStrength(values.newPassword)
    await requests.users.updateUser(values.oldPassword, { newPassword: values.newPassword })
    closePopup()
}

export const deleteUser = async () => {
    const values = getFormValuesById("delete-user-form")
    await requests.users.deleteUser(values.password)
    closePopup()
    requests.session.deleteToken()
    location.hash = "auth"
}
