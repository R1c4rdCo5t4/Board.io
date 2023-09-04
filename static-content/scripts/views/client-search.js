import requests from "../api-requests.js"
import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, icon } from "../utils/dom-elements.js"

let data

const search = () => {
    const query = document.getElementById("search-bar").value
    window.location.hash = "search" + (query ? `?query=${query}` : "")

    // cancel the data promise
    data = new Promise((resolve) => {
        resolve(null)
    })
}

export const renderSearchBar = () =>
    form({ class: "search-bar"},
        label({ for: "search-input" }),
        input({ id: "search-bar", type: "text", placeholder: "Search...", name: "search-input", text: ""}).also(addSearchDropdownEventListener),
        button({ class: "confirm", id: "advanced-search-button" }, search, icon("search"))
    )

const addSearchDropdownEventListener = (input) => {

    const waitForData = async () => {
        if (!data) {
            await new Promise((resolve) => {
                const promise = setInterval(() => {
                    if (data) {
                        clearInterval(promise)
                        resolve()
                    }
                }, 100) // check every 100ms
            })
        }
    }

    input.addEventListener("focus", async () => {
        data = await fetchData()
    })

    input.addEventListener("keyup", async () => {
        await waitForData()

        if(input.value === "" || await data === null) {
            const dropdown = document.querySelector(".dropdown")
            if(dropdown) dropdown.remove()
            return
        }

        const matches = { users: [], boards: [], lists: [], cards: [] }
        Object.entries(data).forEach(([type, values]) => {
            values.forEach((value) => {
                if (value.name.toLowerCase().includes(input.value.toLowerCase())) {
                    matches[type].push(value)
                }
            })
        })
        const results = Object.values(matches).every(values => values.length === 0) ?
            [ p({class: "dropdown-item"}, "No results found") ]
            : Object.keys(matches).map((type) =>
                matches[type].length > 0 ?
                    div({},
                        p({}, type.capitalize()),
                        ...matches[type].map(item => li({ class: "dropdown-item" }, a({ href: item.href }, item.name)))
                    )
                : null
            )

        const pos = input.getBoundingClientRect()
        const dropdown = div({ class: "dropdown", id:"search-dropdown", style: `top:${pos.y}px; left: ${pos.x}px;` },
            ul({ class: "search dropdown-content" },
                ...results
            )
        )

        const mainContent = document.getElementById("main-content")
        const prevDropdown = document.querySelector(".dropdown")
        prevDropdown ? prevDropdown.replaceWith(dropdown) : mainContent.appendChildren(dropdown)

        document.addEventListener("click", (e) => {
            if (e.target !== input) {
                dropdown.remove()
            }
        })
    })
}


const fetchData = async () => {
    const users = (await requests.users.getUsers())
        .map(user => ({ name: user.name, href: "#users/" + user.id }))

    const boards = (await requests.boards.getUserBoards())
        .map(board => ({ ...board, href: "#boards/" + board.id }))

    const lists = (await Promise.all(boards
        .map(board => requests.boards.getBoardLists(board.id)))).flat()
        .map(list => ({ ...list, href: "#lists/" + list.id }))

    const cards = (await Promise.all(lists
        .map(async list => await requests.lists.getListCards(list.id)))).flat()
        .map(card => ({ ...card, href: "#cards/" + card.id })).flat()

    return { users, boards, lists, cards }
}


