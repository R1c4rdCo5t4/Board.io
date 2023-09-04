package pt.isel.ls.boardio

import pt.isel.ls.boardio.app.AppServer

const val JDBC_DATABASE_URL = "JDBC_DATABASE_URL"
const val PORT = "PORT"

fun main() {
    val url = System.getenv(JDBC_DATABASE_URL)
    val port = System.getenv(PORT)?.toIntOrNull()
    requireNotNull(url) { "'$JDBC_DATABASE_URL' environment variable not set" }
    requireNotNull(port) { "'$PORT' environment variable not set" }
    AppServer(port, url).also { it.start() }
}
