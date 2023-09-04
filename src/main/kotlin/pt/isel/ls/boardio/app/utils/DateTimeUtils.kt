package pt.isel.ls.boardio.app.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.sql.Timestamp

fun now() = java.time.LocalDateTime.now().toKotlinLocalDateTime()
fun LocalDateTime.before(other: LocalDateTime) = this < other
fun LocalDateTime.after(other: LocalDateTime) = this > other
fun LocalDateTime.toTimestamp(): Timestamp = Timestamp.valueOf(toJavaLocalDateTime())

fun String.toLocalDateTime(): LocalDateTime {
    val dateString = this.substring(0, 19).replace(" ", "T")
    return LocalDateTime.parse(dateString)
}
