package pt.isel.ls.boardio.domain

import kotlinx.datetime.LocalDateTime

interface Domain {
    val id: Int
    val name: String
    val createdDate: LocalDateTime
}
