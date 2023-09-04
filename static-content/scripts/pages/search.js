import { renderSearch, search } from "../views/api-search.js"
import { getQueriesFromHash } from "../utils/hash.js"

export async function searchPage() {
    const queries = getQueriesFromHash()
    document.getElementById("main-page").replaceChildren(
        renderSearch(queries.query || "", [])
    )
    if(queries.query) await search()
}
