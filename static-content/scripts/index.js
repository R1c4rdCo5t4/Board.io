import router from "./router.js"
import {} from "./utils/extensions.js"
import { homePage } from "./pages/home.js"
import { boardPage } from "./pages/board.js"
import { userPage } from "./pages/user.js"
import { authPage } from "./pages/auth.js"
import { notFoundPage } from "./pages/notfound.js"
import { searchPage } from "./pages/search.js"
import { cardRedirect } from "./pages/card.js"
import { listRedirect } from "./pages/list.js"
import { settingsPage } from "./pages/settings.js"
import { aboutPage } from "./pages/about.js"
import { updateTheme } from "./views/theme.js"

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){
    router.addRouteHandler("home", homePage)
    router.addRouteHandler("users", userPage)
    router.addRouteHandler("boards", boardPage)
    router.addRouteHandler("lists", listRedirect)
    router.addRouteHandler("cards", cardRedirect)
    router.addRouteHandler("auth", authPage)
    router.addRouteHandler("search", searchPage)
    router.addRouteHandler("settings", settingsPage)
    router.addRouteHandler("about", aboutPage)
    router.addDefaultNotFoundRouteHandler(notFoundPage)
    hashChangeHandler()
    updateTheme()
}

function hashChangeHandler() {
    const path = window.location.hash.replace("#", "")
    if(path === "") return window.location.hash = "home"
    const handler = location.pathname === "/" ? router.getRouteHandler(path) : router.getNotFoundHandler()
    handler()
}