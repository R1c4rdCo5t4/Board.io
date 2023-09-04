
let theme = "dark"

function getTheme(){
    return localStorage.getItem("theme")
}

function setTheme() {
    localStorage.setItem("theme", theme)
}

export function updateTheme() {
    const stored = getTheme()
    if (stored === "light") changeTheme()
}

export const changeTheme = () => {
    const root = document.querySelector(":root")
    if (theme === "dark"){
        lightTheme(root)
    } else {
        darkTheme(root)
    }
    theme = theme === "dark" ? "light" : "dark"
    setTheme(theme)
}

const darkTheme = (root) => {
    root.style.setProperty("--border-color", "rgba(255, 255, 255, 0.1)")
    root.style.setProperty("--primary-color", "rgb(200, 200, 200)")
    root.style.setProperty("--background-color", "rgb(32, 32, 32)")
    root.style.setProperty("--secondary-color", "rgb(30, 30, 30)")
    root.style.setProperty("--secondary-background-color", "rgb(28, 28, 28)")
    root.style.setProperty("--lighter-background-color", "rgb(35, 35, 35)")
    root.style.setProperty("--darker-background-color", "rgb(60, 60, 60)")
    root.style.setProperty("--gray", "rgb(80, 80, 80)")
    root.style.setProperty("--darker-gray", "rgb(120, 120, 120)")
}

const lightTheme = (root) => {
    root.style.setProperty("--border-color", "rgba(0, 0, 0, 0.1)")
    root.style.setProperty("--primary-color", "rgb(50, 50, 50)")
    root.style.setProperty("--background-color", "rgb(255, 255, 255)")
    root.style.setProperty("--secondary-color", "rgb(240, 240, 240)")
    root.style.setProperty("--secondary-background-color", "rgb(250, 250, 250)")
    root.style.setProperty("--lighter-background-color", "rgb(245, 245, 245)")
    root.style.setProperty("--darker-background-color", "rgb(220, 220, 220)")
    root.style.setProperty("--gray", "rgb(160, 160, 160)")
    root.style.setProperty("--darker-gray", "rgb(120, 120, 120)")
}
