
document.getElementById("main-content").onmousemove = (e) => {
    document.querySelectorAll(".list").forEach((list) => {
        const rect = list.getBoundingClientRect(),
            x = e.clientX - rect.left,
            y = e.clientY - rect.top
        
        list.style.setProperty('--mouse-x', `${x}px`)
        list.style.setProperty('--mouse-y', `${y}px`)
    })
}
