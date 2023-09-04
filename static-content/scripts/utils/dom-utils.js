import { icon, p, span } from "./dom-elements.js"
import { alertPopup } from "../views/alert.js"

export function getFormValuesById(formId) {
    const form = document.getElementById(formId)
    const formData = new FormData(form)
    const formValues = Object.fromEntries(formData.entries())

    // check for missing required fields
    const requiredFields = form.querySelectorAll("[required]")
    for (const field of requiredFields) {
        const fieldName = field.getAttribute("name")
        if (!formValues[fieldName]) {
            const errorMessage = `Field "${fieldName.splitCamelCase()}" is required.`
            alertPopup(errorMessage)
            throw new Error(errorMessage)
        }
    }

    // check for invalid fields
    const invalidField = form.querySelector(":invalid")
    if (invalidField) {
        const fieldName = invalidField.getAttribute("name")
        const errorMessage = `Invalid ${fieldName.splitCamelCase()}`
        alertPopup(errorMessage)
        throw new Error(errorMessage)
    }
    return formValues
}

export function updateElementValue(element, value) {
    const currentValue = parseInt(element.textContent)
    if (isNaN(currentValue)) throw "Element value is not a number"
    element.textContent = currentValue + value
}

export function updateElementValueById(elementId, value) {
    if (!elementId) throw "Element id is required"
    const element = document.getElementById(elementId)
    if (!element) throw "Element not found"
    updateElementValue(element, value)
}

export function getFormOptionsByQuerySelector(query, defaultValues) {
    const element = document.querySelector(query)
    if (!element) return
    const inputs = [...element.querySelectorAll('input[type="radio"]')]
    const checkboxes = [...element.querySelectorAll('input[type="checkbox"]')]
    if (checkboxes.filter(it => it.checked).length !== 4) inputs.push(...checkboxes)

    const values = {}
    inputs.forEach((input) => {
        if (input.checked && !defaultValues.includes(input.value)) {
            if (!values[input.name]) {
                values[input.name] = []
            }
            values[input.name].push(input.value)
        }
    })
    return values
}

export const removeDraggingShadow = (e) => {
    e.dataTransfer.setDragImage(e.target, window.outerWidth, window.outerHeight)
}
export const listItem = (iconClass, key, value, id) =>
    p({}, icon(iconClass), span({ class: "bold" }, key), span({ id: id || "", class: "value" }, value))