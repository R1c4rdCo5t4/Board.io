package pt.isel.ls.boardio.app.utils

fun <T> List<T>.subSequence(skip: Int?, limit: Int?): List<T> {
    val from = skip ?: 0
    val to = limit?.let { from + it } ?: size
    if (from > size) return emptyList()
    if (to > size) return subList(from, size)
    return subList(from, to)
}
