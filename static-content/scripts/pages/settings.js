import { renderSettings } from "../views/settings.js"

export async function settingsPage() {
    document.getElementById("main-page").replaceChildren(
        renderSettings()
    )
}