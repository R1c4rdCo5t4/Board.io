package pt.isel.ls.boardio.domain.users.database

import pt.isel.ls.boardio.app.database.database.checkIfNotExists
import pt.isel.ls.boardio.app.database.database.checkIfUserAlreadyExists
import pt.isel.ls.boardio.app.database.database.execute
import pt.isel.ls.boardio.app.database.database.executeQueries
import pt.isel.ls.boardio.app.database.database.executeQuery
import pt.isel.ls.boardio.app.database.database.getValuesToUpdate
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.ForbiddenException
import pt.isel.ls.boardio.app.utils.hashPassword
import pt.isel.ls.boardio.app.utils.subSequence
import pt.isel.ls.boardio.app.utils.toLocalDateTime
import pt.isel.ls.boardio.app.utils.verifyPassword
import pt.isel.ls.boardio.domain.users.User
import java.util.UUID

class UsersDatabase : UsersSource {

    override fun createUser(source: Source, name: String, email: String, password: String): Pair<Int, String> {
        checkIfUserAlreadyExists(source, name, email)
        val stm = "insert into \"user\" (name, email, token, password) values (?,?,?,?) returning id"
        val token = UUID.randomUUID().toString()
        val hashedPassword = hashPassword(password)
        val args = listOf(name, email, token, hashedPassword)
        val columns = listOf("id")
        val (userId) = source.conn.executeQuery(stm, args, columns)
        return Pair(userId.toInt(), token)
    }

    override fun getUserById(source: Source, userId: Int): User {
        val stm = "select name, email, token, createdDate from \"user\" where id = ?"
        val args = listOf(userId)
        val (name, email, token, createdDate) = source.conn.executeQuery(stm, args) { "User with id $userId not found" }
        return User(userId, name, email, token, null, createdDate.toLocalDateTime())
    }

    override fun getUserByToken(source: Source, token: String): User {
        val stm = "select name, email, id, createdDate from \"user\" where token = ?"
        val args = listOf(token)
        val (name, email, id, createdDate) = source.conn.executeQuery(stm, args) { "User with token $token not found" }
        return User(id.toInt(), name, email, token, null, createdDate.toLocalDateTime())
    }

    override fun loginUser(source: Source, email: String, password: String): Pair<Int, String> {
        val stm = "select id, token from \"user\" where email = ? and password = ?"
        val args = listOf(email, hashPassword(password))
        val (id, token) = source.conn.executeQuery(stm, args) { "Incorrect e-mail or password" }
        return Pair(id.toInt(), token)
    }

    override fun getUsers(source: Source, skip: Int?, limit: Int?): List<User> {
        val stm = "select id, name, email, token, createdDate from \"user\""
        val args = emptyList<String>()
        val results = source.conn.executeQueries(stm, args)
        return results.map { user ->
            val (id, name, email, token, createdDate) = user
            User(id.toInt(), name, email, token, null, createdDate.toLocalDateTime())
        }.subSequence(skip, limit)
    }

    override fun getUserByName(source: Source, name: String): User {
        val stm = "select email, token, id, createdDate from \"user\" where name = ?"
        val args = listOf(name)
        val (email, token, id, createdDate) = source.conn.executeQuery(stm, args) { "User $name not found" }
        return User(id.toInt(), name, email, token, null, createdDate.toLocalDateTime())
    }

    override fun updateUser(source: Source, token: String, password: String, name: String?, email: String?, newPassword: String?) {
        checkIfUserAlreadyExists(source, name, email)
        checkUserPassword(source, token, password)
        val (columns, valuesToUpdate) = getValuesToUpdate(
            "name" to name,
            "email" to email,
            "password" to newPassword?.let { hashPassword(it) }
        )
        val stm = "update \"user\" set $columns where token = ?"
        val args = listOf(*valuesToUpdate, token)
        source.conn.execute(stm, args)
    }

    override fun deleteUser(source: Source, token: String, password: String) {
        checkUserPassword(source, token, password)
        val stm = "delete from \"user\" where token = ? and password = ?"
        val args = listOf(token, hashPassword(password))
        source.conn.execute(stm, args)
    }

    private fun checkUserPassword(source: Source, token: String, password: String) {
        source.conn.checkIfNotExists("\"user\"", "token", token) { "User with token $token not found" }
        val stm = "select password from \"user\" where token = ?"
        val args = listOf(token)
        val (hashedPassword) = source.conn.executeQuery(stm, args) { "User with token $token not found" }
        if (!verifyPassword(password, hashedPassword)) {
            throw ForbiddenException("Incorrect password")
        }
    }
}
