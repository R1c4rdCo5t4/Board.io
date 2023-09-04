
export function formatSimpleDate(dateString) {
    if (!dateString) return ""
    const date = new Date(dateString)
    const options = { day: 'numeric', month: 'short' }
    return date.toLocaleDateString('en-US', options)
}

export function formatFullDate(dateString) {
    if (!dateString) return ""
    const date = new Date(dateString)
    const options = { day: "numeric", month: "short", hour: "2-digit", minute: "2-digit" }
    return date.toLocaleDateString("en-US", options)
}

export function getCurrentTime() {
    const date = new Date()
    return date.toISOString().slice(11, 16)
}

export function getNextDayDate() {
    const date = new Date()
    date.setDate(date.getDate() + 1)
    return date.toISOString().slice(0, 10)
}

export function dateIsOverdue(dateString) {
    if (!dateString) return false
    const date = new Date(dateString)
    const today = new Date()
    return date < today
}
