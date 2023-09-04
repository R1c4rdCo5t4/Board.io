package pt.isel.ls.boardio.app.database.source

interface Source {
    val conn get() = (this as DatabaseSource).conn
    val mem get() = (this as MemSource).mem
    fun authenticateUser(token: String): Int
}
