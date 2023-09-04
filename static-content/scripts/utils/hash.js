
export function getIdFromHash() {
    const fragment = window.location.hash.slice(1)
    const views = fragment.split("/")
    const id = views[views.length - 1]
    return parseInt(id)
}

export function getQueriesFromHash() {
    const hash = window.location.hash.slice(1)
    const queryString = hash.split('?')[1]
    const searchParams = new URLSearchParams(queryString)
    const queries = {}
    for (const [key, value] of searchParams.entries()) {
        if (queries[key]) {
            queries[key] = [...queries[key], value]
        } else {
            queries[key] = value
        }
    }
    return queries
}