import { require } from "../api-requests.js"


export function objectToURIQueries(object){
    let queries = ""
    for (const [key, value] of Object.entries(object)) {
        const val = Array.isArray(value) ? value.map(s => s.removeSpaces()).join(",") : value.removeSpaces()
        const result = replaceProperties(val, { "ascending": "asc", "descending": "desc", "length": "len", "createddate": "created" })
        queries += `&${key}=${result}`
    }
    return queries
}

export function replaceProperties(string, object) {
    for (const [key, value] of Object.entries(object)) {
        string = string.replace(key, value)
    }
    return string
}

export function checkPasswordStrength(password) {
    require(password.length >= 8, "The passwords should be at least 8 characters long")
    require(/[A-Z]/.test(password), "The password should contain at least one uppercase letter")
    require(/[a-z]/.test(password), "The password should contain at least one lowercase letter")
    require(/[0-9]/.test(password), "The password should contain at least one number")
    require(/[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(password), "The password should contain at least one special character")
}


document.addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
        const activeElement = document.activeElement
        if (activeElement.tagName === "INPUT") {
            event.preventDefault()
            activeElement.parentElement.querySelector(".confirm")?.click()
        }
    }
})

document.addEventListener("submit", function(event) {
    event.preventDefault()
})
