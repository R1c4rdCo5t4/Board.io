import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../utils/dom-elements.js"
import requests from "../api-requests.js"
import { popupButton, closePopup } from "./popup.js"
import { MAX_EMAIL_SIZE, MAX_NAME_SIZE, MAX_PASSWORD_SIZE, MIN_PASSWORD_SIZE } from "../utils/constants.js"
import { getLoggedUser } from "../domain/users/user-operations.js"
import {getFormValuesById} from "../utils/dom-utils.js"

export const authenticateUser = async () => {
    const token = requests.session.getToken()
    if(!token) {
        window.location.hash = "auth"
        return null
    }
    return await getLoggedUser()
}

const signIn = async (userId, token) => {
    requests.session.setToken(token)
    closePopup()
    window.location.hash = "home"
}

export const renderAuthPage = () =>
    div({ class: "overlay" },
        img({ src: "../../images/logo.png", alt: "Board.io Logo" }),
        h1({}, "Welcome to Board.io!"),
        h3({}, "A simple & visual way to organize tasks"),
        div({ class: "overlay-buttons" },
            popupButton({ id: "signup-button" }, renderSignUpPopup, "Sign Up"),
            popupButton({ id: "login-button" }, renderLoginPopup, "Login")
        )
    )

const renderSignUpPopup = () =>
    div({ class: "popup" },
        form({ id: "signup-form", class: "form-container" },
            h1({}, "Sign Up"),
            label({ for: "name" }, "Username:"),
            input({ id: "signup-username", name: "name", maxlength: MAX_NAME_SIZE }, true),
            label({ for: "email" }, "Email:"),
            input({ id: "signup-email", name: "email", maxlength: MAX_EMAIL_SIZE }, true),
            label({ for: "password" }, "Password:"),
            input({ class: "password", id: "signup-password", name: "password", type: "password", maxlength: MAX_PASSWORD_SIZE, minlength: MIN_PASSWORD_SIZE }, true),
            span({ id:"password-strength" }),
            button({ class: "confirm" }, signUpUser, "Sign Up")
        )
    )

const renderLoginPopup = () =>
    div({ class:"popup" },
        form({ id: "login-form", class: "form-container" },
            h1({}, "Login"),
            label({ for: "email" }, "Email:"),
            input({ id: "login-email", maxlength: MAX_EMAIL_SIZE, name: "email" }),
            label({ for: "password" }, "Password:"),
            input({ id: "login-password", maxlength: MAX_PASSWORD_SIZE, name: "password", type: "password" }, true),
            button({ class: "confirm" }, loginUser, "Login")
        )
    )

const signUpUser = async () => {
    const values = getFormValuesById("signup-form")
    const response = await requests.users.createUser(values.name, values.email, values.password)
    await signIn(response.userId, response.token)
}

const loginUser = async () => {
    const values = getFormValuesById("login-form")
    const response = await requests.users.loginUser(values.email, values.password)
    await signIn(response.userId, response.token)
}