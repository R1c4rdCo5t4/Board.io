package pt.isel.ls.boardio.domain.users.database

import pt.isel.ls.boardio.app.database.datamem.checkIfAlreadyExists
import pt.isel.ls.boardio.app.database.datamem.getNextId
import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.app.utils.ForbiddenException
import pt.isel.ls.boardio.app.utils.NotFoundException
import pt.isel.ls.boardio.app.utils.hashPassword
import pt.isel.ls.boardio.app.utils.subSequence
import pt.isel.ls.boardio.app.utils.verifyPassword
import pt.isel.ls.boardio.domain.users.User
import java.util.UUID.randomUUID

class UsersDataMem : UsersSource {
    override fun createUser(source: Source, name: String, email: String, password: String): Pair<Int, String> {
        checkIfAlreadyExists(source.mem.users, name, User::name)
        val id = source.mem.users.keys.getNextId()
        val token = randomUUID().toString()
        val hashedPassword = hashPassword(password)
        source.mem.users[id] = User(id, name, email, token, hashedPassword)
        return Pair(id, token)
    }

    override fun getUserById(source: Source, userId: Int): User {
        return source.mem.users[userId] ?: throw NotFoundException("User with id $userId not found")
    }

    override fun getUserByToken(source: Source, token: String): User {
        return source.mem.users.values.find { it.token == token }
            ?: throw NotFoundException("User with token $token not found")
    }

    override fun loginUser(source: Source, email: String, password: String): Pair<Int, String> {
        val user = source.mem.users.entries.find { it.value.email == email }
            ?: throw NotFoundException("User with e-mail $email not found")
        if (!verifyPassword(password, user.value.password!!)) throw ForbiddenException("Incorrect password")
        val id = user.key
        val token = user.value.token
        return Pair(id, token)
    }

    override fun getUsers(source: Source, skip: Int?, limit: Int?) =
        source.mem.users.values.toList().subSequence(skip, limit)

    override fun getUserByName(source: Source, name: String) =
        source.mem.users.values.find { it.name == name }
            ?: throw NotFoundException("User with username $name not found")

    override fun updateUser(source: Source, token: String, password: String, name: String?, email: String?, newPassword: String?) {
        val user = source.mem.users.values.firstOrNull { it.token == token }
            ?: throw NotFoundException("User with token $token not found")
        if (!verifyPassword(password, user.password!!)) throw ForbiddenException("Incorrect password")
        if (name != null) checkIfAlreadyExists(source.mem.users, name, User::name)
        if (email != null) checkIfAlreadyExists(source.mem.users, email, User::name)

        source.mem.users[user.id] = user.copy(
            name = name ?: user.name,
            email = email ?: user.email,
            password = newPassword?.let { hashPassword(it) } ?: user.password
        )
    }

    override fun deleteUser(source: Source, token: String, password: String) {
        val user = source.mem.users.entries.find { it.value.token == token } ?: throw NotFoundException("User not found")
        if (!verifyPassword(password, user.value.password!!)) throw ForbiddenException("Incorrect password")
        source.mem.users.remove(user.key)
    }
}
