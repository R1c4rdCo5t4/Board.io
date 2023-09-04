import requests from '../api-requests.js'
import { renderAuthPage } from '../views/auth.js'

export const authPage = () => {
    if(requests.session.getToken()) return window.location.hash = "home"
    document.getElementById("main-page").replaceChildren(
        renderAuthPage()
    )
}