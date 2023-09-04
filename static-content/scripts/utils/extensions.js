HTMLElement.prototype.appendChildren = function (...children) {
    children.forEach(child => this.appendChild(child))
}

String.prototype.capitalize = function () {
    return (this.charAt(0).toUpperCase() + this.slice(1)).replaceAll("-", " ")
}

String.prototype.splitCamelCase = function () {
    return this.replace(/([a-z])([A-Z])/g, '$1 $2').toLowerCase()
}

String.prototype.removeSpaces = function () {
    return this.toLowerCase().replaceAll(" ", "")
}

String.prototype.splitBy = function(separators) {
    let str = this
    const delimiter = '|'
    separators.split("").forEach(sep => { str = str.split(sep).join(delimiter) })
    return str.split(delimiter)
}

Array.prototype.filterArchived = function () {
    return this.filter(it => it.archived)
}

Array.prototype.filterNotArchived = function () {
    return this.filter(it => !it.archived)
}

Object.prototype.filterNotNullProperties = function () {
    return Object.fromEntries(
        Object.entries(this).filter(([_, value]) => value !== null)
    )
}

Object.prototype.also = function (block) {
    block(this)
    return this
}