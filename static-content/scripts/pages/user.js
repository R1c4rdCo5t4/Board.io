import requests from "../api-requests.js"
import { renderUserDetails } from "../domain/users/user-views.js"
import { getIdFromHash } from "../utils/hash.js"

export async function userPage() {
    const profileUserId = getIdFromHash()
    const profileUser = await requests.users.getUser(profileUserId)
    const userBoards = await requests.boards.getUserBoards(profileUser.token)
    document.getElementById("main-page").replaceChildren(
        renderUserDetails(profileUser, userBoards),
    )
}