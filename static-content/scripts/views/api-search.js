import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../utils/dom-elements.js"
import { searchAPI } from "../api-requests.js"
import { getFormOptionsByQuerySelector } from "../utils/dom-utils.js"

let skip = 0
let limit = 15
const icons = {
    "users": "user",
    "boards": "trello",
    "lists": "list",
    "cards": "check-square-o"
}

export const renderSearch = (query, results) =>
    div({ class: "search-container" },
        div({ class: "search-content"},
            div({ class: "search"},
                form({ class: "search-form" },
                    h1({}, "Search"),
                    form({ class: "search-bar" },
                        label({ for: "query" }),
                        input({ id: "search-input", type: "text", placeholder: "Search...", name: "query", value: query }),
                        button({ id: "search-button", class: "confirm" }, search, icon("search")),
                    ).also(form => form.addEventListener("submit", e => e.preventDefault())),
                ),
                div({ class: "search-results-title" }, renderSearchResultsTitle(results, query)),
                div({ class: "search-results" }, ...renderSearchResults(results)),
            ),
           renderSearchPagination()
        ),
        renderSearchOptions()
    )

const renderSearchPagination = (total) =>
    div({ class: "search-pagination" },
        button({ class: "fa fa-chevron-left", style: "visibility: " + (skip !== 0 ? "visible;" : "hidden;") }, async () => {
            skip -= limit
            await search()
        }),
        p({ id: "page-number" }, "Page " + (skip / limit + 1)),
        button({ class: "fa fa-chevron-right", style: "visibility: " + (skip < total - limit ? "visible;" : "hidden;") }, async () => {
            skip += limit
            await search()
        })
    )

export const search = async () => {
    const query = document.getElementById("search-input").value
    if(!query) return

    const defaultValues = [["Users", "Boards", "Lists", "Cards"], "Name", "Ascending"]
    const options = getFormOptionsByQuerySelector(".search-options", defaultValues)
    const { results, total } = await searchAPI(query, skip, limit, options)

    document.querySelector(".search-results").replaceChildren(...renderSearchResults(results))
    document.querySelector(".search-results-title").replaceChildren(renderSearchResultsTitle(results, query))
    document.querySelector(".search-pagination").replaceWith(renderSearchPagination(total))
    if(results.length > 0) {
        document.getElementById("query").textContent = query
        document.getElementById("total-results").textContent = `(${total})`
    }
}

export const renderSearchResultsTitle = (results, query) =>
    results.length > 0 ?
        p({}, "Search results for: ",
            span({ id: "query", class: "bold" }, query),
            span({ id: "total-results" })
        )
    : p({}, "No results")

export const renderSearchResults = (results) =>
    results.map(result =>
        div({ class: "search-result" },
            a({ href: "#" + result.type + "/" + result.id }, icon(icons[result.type]), result.name)
        )
    )


export const renderSearchOptions = () =>
    div({ class: "search-options" },
        div({ class: "filter-type" },
            h3({}, "Filter by type"),
            form({ id: "filter-by-type-form" },
                ...["Users", "Boards", "Lists", "Cards"].map(type =>
                    div({ class: "filter-type-option" },
                        label({ for: "type" }, type),
                        input({ id: "filter-type-" + type, type: "checkbox", name: "type", value: type, checked: true }),
                    )
                )
            )
        ),
        div({ class: "sort-by" },
            h3({}, "Sort by"),
            form({ id: "sort-by-form" },
                ...["Name", "Created date", "Length"].map(sort =>
                    div({ class: "sort-by-option" },
                        label({ for: "sortby" }, sort),
                        input({ id: "sort-by", type: "radio", name: "sortby", value: sort, ...(sort === "Name" ? { checked: true } : {}) }),
                    )
                )
            )
        ),
        div({ class: "order-by" },
            h3({}, "Order by"),
            form({ id: "order-by-form" },
                ...(["Ascending", "Descending"].map(order =>
                    div({ class: "order-by-option" },
                        label({ for: "orderby" }, order),
                        input({ id: "order-by", type: "radio", name: "orderby", value: order, ...(order === "Ascending" ? { checked: true } : {}) })
                    )
                ))
            )
        )
    )

