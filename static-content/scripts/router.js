import requests from "./api-requests.js"
import { authenticateUser } from "./views/auth.js"
import { renderSidebar } from "./views/sidebar.js"
import { renderHeader } from "./views/header.js"

const routes = {}
let notFoundRouteHandler = () => { throw "Route handler for unknown routes not defined" }

function addRouteHandler(path, handler){
    routes[path] = () => middleware(handler)
}

async function middleware(next) {
    const user = await authenticateUser()
    if(!user) return next()
    const boards = await requests.boards.getUserBoards()
    renderSidebar(user, boards)
    renderHeader(user)
    next()
}


function addDefaultNotFoundRouteHandler(handler) {
    notFoundRouteHandler = handler
}

function getRouteHandler(path) {
    const basePath = path.splitBy("/?")[0]
    const route = routes[basePath]
    return route || notFoundRouteHandler
}

function getNotFoundHandler() {
    return notFoundRouteHandler
}

const router = {
    addRouteHandler,
    getRouteHandler,
    addDefaultNotFoundRouteHandler,
    getNotFoundHandler
}

export default router