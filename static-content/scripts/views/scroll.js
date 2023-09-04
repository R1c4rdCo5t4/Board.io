
export function horizontalMouseScroll() {
    const board = document.querySelector('.board-content')
    const container = document.getElementById('board-container')

    let isDragging = false
    let startX, scrollLeft

    board.addEventListener('mousedown', e => {
        if (e.target !== board && e.target !== container) return
        isDragging = true
        startX = e.pageX - board.offsetLeft
        scrollLeft = board.scrollLeft
    })

    board.addEventListener('mouseleave', (e) => {
        isDragging = false
    })

    board.addEventListener('mouseup', (e) => {
        isDragging = false
    })

    board.addEventListener('mousemove', e => {
        if (!isDragging) return
        e.preventDefault()
        e.stopPropagation()
        const x = e.pageX - board.offsetLeft
        const walk = x - startX
        board.scrollLeft = scrollLeft - walk
    })
}