import requests from "../api-requests.js"
import { renderHome } from "../views/home.js"

export async function homePage() {
    const boards = await requests.boards.getUserBoards()
    document.getElementById("main-page").replaceChildren(
        renderHome(boards),
    )
}
