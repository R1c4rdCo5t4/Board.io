package pt.isel.ls.boardio.app.database.source

import pt.isel.ls.boardio.app.database.datamem.DataMem
import pt.isel.ls.boardio.app.utils.UnauthorizedException

class MemSource : Source {
    override val mem = DataMem()

    override fun authenticateUser(token: String): Int {
        val user = mem.users.values.firstOrNull { it.token == token }
        return user?.id ?: throw UnauthorizedException("User with token $token was not found")
    }
}
