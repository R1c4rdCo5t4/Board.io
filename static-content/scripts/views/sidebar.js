import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, icon } from "../utils/dom-elements.js"
import {listItem} from "../utils/dom-utils.js"

export const renderSidebar = (user, userBoards) => {
    document.getElementById("sidebar").replaceWith(
        div({ id: "sidebar" },
            div({ id: "sidebar-content" },
                p({ id: "workspace-owner" }, user.name + "'s Workspace"),
                renderSidebarBoards(userBoards)
            ),
            button({ class: "toggle-sidebar" }, toggleSidebar, icon("chevron-left"))
        )
    )
}

export const renderSidebarBoards = (userBoards) =>
    div({ class: "sidebar-boards" },
        h2({}, "Boards"),
        div({ id: "user-boards" },
            ...userBoards.map(board =>
                a({ href: "#boards/" + board.id },
                    p({ class: "board-name", id: "sidebar-board-" + board.id }, board.name),
                )
            )
        )
    )

export const renderSidebarBoardInfo = (boardUsers, boardLists, boardCards) =>
    div({ class: "sidebar-board-info" },
        div({},
            h2({}, "Board Users"),
            div({ id: "board-users" }, ...boardUsers.map(renderSidebarBoardUser))
        ),
        div({ class: "board-stats" },
            listItem("users", "Members:", boardUsers.length || "0", "board-users-amount"),
            listItem("list", "Lists:", boardLists.flat().length || "0", "board-lists-amount"),
            listItem("check-square-o", "Cards:", boardCards.flat().length || "0", "board-cards-amount")
        )
    )


export const renderSidebarBoardUser = (user) =>
    div({ class: "board-user", id: "board-user-" + user.id },
        a({ href: "#users/" + user.id }, user.name)
    )


const toggleSidebar = () => {
    const sidebar = document.getElementById("sidebar-content")
    const sidebarToggle = document.querySelector(".toggle-sidebar")
    const sidebarIcon = sidebarToggle.children[0]
    const menubar = document.querySelector(".menu-bar")
    const minSidebarWidth = "2.5vw"

    if(sidebar.style.width === minSidebarWidth){ // open
        sidebar.style.visibility = "visible"
        sidebar.style.width = "18vw"
        sidebarToggle.style.left = "16%"
        sidebarIcon.classList.replace("fa-chevron-right", "fa-chevron-left")
        if(menubar) menubar.style.width = "81vw"

    } else { // close
        sidebar.style.visibility = "hidden"
        sidebar.style.width = minSidebarWidth
        sidebarToggle.style.left = "0.75%"
        sidebarIcon.classList.replace("fa-chevron-left", "fa-chevron-right")
        if(menubar) menubar.style.width = "96.65%"
    }
}