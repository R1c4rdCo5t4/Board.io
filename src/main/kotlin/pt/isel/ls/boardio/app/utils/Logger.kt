package pt.isel.ls.boardio.app.utils

import org.http4k.core.Request
import org.http4k.core.Response
import org.slf4j.Logger

fun Logger.log(request: Request) {
    if (request.bodyString().isNotEmpty()) {
        info(
            "incoming request: method={}, uri={}, body={}",
            request.method,
            request.uri,
            request.bodyString()
        )
    } else {
        info(
            "incoming request: method={}, uri={}",
            request.method,
            request.uri
        )
    }
}
fun Logger.log(response: Response) {
    info(
        "outgoing response: status={}, body={}",
        response.status,
        response.bodyString()
    )
}

fun getProjectStackTrace(e: Exception): List<StackTraceElement> {
    val packageName = "pt.isel.ls.boardio"
    val stacktrace = e.stackTrace.filter { it.className.startsWith(packageName) }
    return if (e is ApplicationException) stacktrace.subList(0, 1) else stacktrace.subList(1, stacktrace.size)
}

fun Logger.log(e: Exception) {
    val stackTrace = getProjectStackTrace(e)
    val separator = "\n\tat "
    error(
        "{} - {} $separator{}",
        e.javaClass.simpleName,
        e.message,
        stackTrace.joinToString(separator)
    )
}
