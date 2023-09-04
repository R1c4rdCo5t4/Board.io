import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../utils/dom-elements.js"


export const renderDropdown = (clickable, event, position, ...items) => {
    // add event listener for mouse event
    clickable.addEventListener(event, (e) => {
        e.preventDefault()
 
        const mainContent = document.getElementById("main-content")
        const prevDropdown = document.querySelector(".dropdown")
        if (prevDropdown) prevDropdown.remove()

        const pos = position || { x: e.clientX + "px", y: e.clientY + "px" }

        const dropdown = div({ class: "dropdown", style: `top: ${pos.y}; left: ${pos.x};` },
            ul({ class: "dropdown-content" },
                ...items.map(item => li({ class: "dropdown-item" }, item))
            )
        )
        mainContent.appendChildren(dropdown)

        // add event listener for click outside of dropdown
        document.addEventListener("click", (e) => {
            if (e.target !== clickable) {
                dropdown.remove()
            }
        })
    })
}
