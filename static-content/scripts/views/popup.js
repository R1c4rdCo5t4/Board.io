import { createElement } from "../utils/dom-elements.js"

let popupActive = false

export function openPopup(popup) {
    if (!popupActive) {    
        document.getElementById("popup-container").replaceChildren(popup)
        popupActive = true
    }
    document.querySelector(".dropdown")?.remove()
}

export function closePopup() {
    document.getElementById("popup-container").replaceChildren()
    popupActive = false
}

export function popupButton(attrs, popup, ...children) {
    const element = createElement("button", attrs, ...children)
    addPopupEventListener(element, popup)
    return element
}

const addPopupEventListener = (button, popupCallback) => {
    button.addEventListener("click", async (e) => {
        if(popupActive) return
        const popup = await popupCallback()
        openPopup(popup)
        popup.querySelector("input")?.focus()
    })

    document.addEventListener("mousedown", (e) => {
        if(!popupActive) return
        const popupContainer = document.getElementById("popup-container")
        const alertPopup = document.getElementById("alert-popup-container")
        if (!popupContainer.contains(e.target) && !button.contains(e.target) && !e.target.closest(".dropdown") && !alertPopup.contains(e.target)) {
            closePopup()
        }
    })

}
