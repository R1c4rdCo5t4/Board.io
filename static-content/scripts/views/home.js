import { h1, h2, h3, h4, h5, h6, p, a, div, img, ul, li, button, small, form, input, span, label, textarea, icon } from "../utils/dom-elements.js"
import { renderBoardPreview, renderAddBoard } from "../domain/boards/board-views.js"
import { addBoardDropdownEventListener } from "../domain/boards/board-operations.js"

export const renderHome = (userBoards) => 
    div({ class: "home-container" },
        h1({ id: "home" }, "Home"),
        h2({}, "Boards"),
        div({ id: "boards-container" },
            ...userBoards.map(board => {
                const boardDiv = renderBoardPreview(board)
                addBoardDropdownEventListener(board, boardDiv)
                return boardDiv
            }),
            renderAddBoard(),

        )
    )
