import { div, form, label, input, button, h1, h2, icon, p } from "../utils/dom-elements.js"
import {renderUserSettings} from "../domain/users/user-views.js"

export const renderSettings = () => 
    div({ class: "settings-container" },
        h1({ id: "settings" }, "Settings"),
        h2({}, "Account"),
        div({ id: "account-container" },
            renderUserSettings()
        )
    )