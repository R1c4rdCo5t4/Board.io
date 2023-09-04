import { p, div, button } from "../utils/dom-elements.js"

const POPUP_TIMEOUT = 6000

export const alertPopup = (message) => {
    document.getElementById("alert-popup-container").replaceChildren(
        div({ class: "alert-popup" },
            p({ class: "bold" }, "Error:"),
            p({}, message),
            button({ class: "fa fa-times"}, removeAlertPopup),
        )
    )
    setTimeout(removeAlertPopup, POPUP_TIMEOUT)
}

const removeAlertPopup = () => document.querySelector(".alert-popup")?.remove()
