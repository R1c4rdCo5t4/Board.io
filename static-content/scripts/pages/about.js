import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../utils/dom-elements.js"

export async function aboutPage() {
    document.getElementById("main-page").replaceChildren(
        div({ class: "about" },
            h1({}, "Board.io"),
            p({}, "Board.io is a project management tool that helps you organize and manage your projects."),
            p({}, "It is a single-page application (SPA) built with vanilla JavaScript and a custom-built RESTful API."),
            p({}, "Made by:"),
            div({ class: "about-authors" },
                a({ href: "https://github.com/R1c4rdCo5t4", target: "_blank" }, icon("github"), "Ricardo Costa"),
                a({ href: "https://github.com/wartuga", target: "_blank" }, icon("github"), "Diogo Almeida"),
            )
        )
    )
}