import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../../utils/dom-elements.js"
import { MAX_NAME_SIZE, MAX_PASSWORD_SIZE, MIN_PASSWORD_SIZE } from "../../utils/constants.js"
import { addUserToBoard, removeUserFromBoard, updateUser, deleteUser, changePassword } from "./user-operations.js"
import { popupButton } from "../../views/popup.js"
import { addManageUsersDropdownEventListener } from "../boards/board-operations.js"
import {getFormValuesById} from "../../utils/dom-utils.js"

export const renderUserDetails = (user, userBoards) =>
    div({ class: "user-details" },
        h3({}, icon("id-badge"), p({ id: "user-username"}, user.name)),
        p({}, icon("at"), p({ id: "user-email"}, user.email)),
        h3({}, "Boards"),
        div({ class: "user-boards" },
            ...userBoards.map(board => 
                a({ href: "#boards/" + board.id },
                    div({ class: "board-preview" },
                        h4({}, board.name),
                    )
                )
            )
        )
    )

export const renderManageUsersPopup = (boardId, boardUsers) =>
    div({ class: "popup" },
        h2({}, "Manage Users"),
        div({ class: "board-users" },
            ...boardUsers.map(user => renderBoardUser(user, boardId))
        ),
        form({ class: "add-user form-container", id: "add-user-form" },
            addManageUsersDropdownEventListener(
                input({ type: "text", name: "name", maxlength: MAX_NAME_SIZE, placeholder: "Username..." })
            ),
            button({ class: "confirm fa fa-user-plus" }, async () => {
                const values = getFormValuesById("add-user-form")
                await addUserToBoard(boardId, values.name)
            })
        )
    )


export const renderBoardUser = (user, boardId) =>
    div({ class: "board-user", id: "user-" + user.id },
        div({}, user.name,
            button({ class: "fa fa-user-xmark" }, async () => await removeUserFromBoard(boardId, user.id))
        )
    )


export const renderUserSettings = () =>
    div({ class: "logged-user-controls" },
        popupButton({}, updateUserPopup, icon("pencil"), "Update Profile"),
        popupButton({}, deleteUserPopup, icon("trash"), "Delete Account"),
        popupButton({}, changePasswordPopup, icon("key"), "Change Password")
    )


const updateUserPopup = () =>
    div({ class: "popup" },
        div({ class: "popup-content"},
            h2({}, "Update Profile"),
            form({ id: "update-user-form", class: "form-container" },
                label({ for: "name" }, "Username"),
                input({ id: "user-name", type: "text", name: "name", maxlength: MAX_NAME_SIZE, placeholder: "Username..." }),
                label({ for: "email" }, "Email"),
                input({ id: "user-email", type: "email", name: "email", placeholder: "Email..." }),
                label({ for: "password" }, "Password"),
                input({ id: "update-user-password", type: "password", name: "password", maxlength: MAX_PASSWORD_SIZE, minlength: MIN_PASSWORD_SIZE, placeholder: "Password..." }),
                button({ class: "confirm" }, updateUser, "Update")
            )
        )
    )

const deleteUserPopup = () =>
    div({ class: "popup" },
        div({ class: "popup-content"},
            h2({}, "Delete Account"),
            form({ id: "delete-user-form", class: "form-container" },
                label({ for: "password" }, "Password"),
                input({ id: "delete-user-password" ,type: "password", name: "password", maxlength: MAX_PASSWORD_SIZE, minlength: MIN_PASSWORD_SIZE, placeholder: "Password..." }),
                button({ class: "confirm" }, deleteUser, "Delete")
            )
        )
    )

const changePasswordPopup = () =>
    div({ class: "popup" },
        div({ class: "popup-content"},
            h2({}, "Change Password"),
            form({ id: "change-password-form", class: "form-container" },
                label({ for: "oldPassword" }, "Old Password"),
                input({ type: "password", name: "oldPassword", placeholder: "Old password..." }),
                label({ for: "newPassword" }, "New Password"),
                input({ type: "password", name: "newPassword", maxlength: MAX_PASSWORD_SIZE, minlength: MIN_PASSWORD_SIZE, placeholder: "New password..." }),
                label({ for: "confirmPassword" }, "Confirm Password"),
                input({ type: "password", name: "confirmPassword", placeholder: "Confirm password..." }),
                button({ class: "confirm" }, changePassword, "Change Password")
            )
        )
    )