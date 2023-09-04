import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, icon } from "../utils/dom-elements.js"
import requests from "../api-requests.js"
import { renderSearchBar } from "./client-search.js"
import { renderDropdown } from "./dropdown.js"
import { changeTheme } from "./theme.js"

export const renderHeader = (user) => {
    document.querySelector("header").replaceChildren(
        div({ class: "header" },
            div({ class: "left-container" },
                a({ href: "#home" },
                    div({ class: "logo" },
                        img({ src: "images/logo.png", alt: "Board.io Logo" }),
                        h1({}, "Board.io")
                    )
                )
            ),
            div({ class: "right-container" },
                renderSearchBar(),
                button({}, changeTheme, icon("circle-half-stroke")),
                renderUserButton(user)
            )
        )
    )
}

export const renderUserButton = (user) => {
    const btn = button({ id: "user-button"}, () => {}, user.name, icon("user"))
    const position = { x: "90%", y: "7.5%" }
    renderDropdown(btn, "click", position,
        button({}, () => { window.location.hash = "users/" + user.id }, icon("user-circle"), "Profile"),
        button({}, () => { window.location.hash = "about" }, icon("info-circle"), "About"),
        button({}, () => { window.location.hash = "settings" }, icon("cog"), "Settings"),
        button({ id: "logout-button" }, () => {
            requests.session.deleteToken()
            window.location.hash = "auth"
        }, icon("right-from-bracket"), "Logout")
    )
    return btn
}
