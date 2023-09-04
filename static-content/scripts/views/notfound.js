import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../utils/dom-elements.js"

export const renderNotFound = () => 
    div({ class: "overlay" },
        img({ src: "../../images/logo.png", alt: "Board.io Logo", class: "background-img" }),
        h1({ class: "not-found"}, "Page Not Found"),
        button({ id: "back-home-button" }, () => {
            window.location.href = "/"
            window.location.hash = "home"
        }, "Back to Home")
    )
