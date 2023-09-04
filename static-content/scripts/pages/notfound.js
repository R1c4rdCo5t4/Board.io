import { renderNotFound } from "../views/notfound.js"

export function notFoundPage() {
    location.hash = "notfound"
    document.getElementById("main-content").replaceChildren(
        renderNotFound()
    )
}