package pt.isel.ls.boardio.app.database.source

import pt.isel.ls.boardio.app.database.database.set
import pt.isel.ls.boardio.app.utils.UnauthorizedException
import java.sql.Connection

class DatabaseSource(override val conn: Connection) : Source {

    override fun authenticateUser(token: String): Int {
        val stm = conn.prepareStatement("select id from \"user\" where token = ?")
        stm.set(token)
        val rs = stm.executeQuery()
        if (!rs.next()) throw UnauthorizedException("User with token $token was not found")
        val userId = rs.getInt("id")
        rs.close()
        stm.close()
        return userId
    }
}
