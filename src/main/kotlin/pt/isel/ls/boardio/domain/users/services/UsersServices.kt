package pt.isel.ls.boardio.domain.users.services

import pt.isel.ls.boardio.app.database.Database
import pt.isel.ls.boardio.app.services.utils.validateEmail
import pt.isel.ls.boardio.app.services.utils.validateId
import pt.isel.ls.boardio.app.services.utils.validateName
import pt.isel.ls.boardio.app.services.utils.validateString
import pt.isel.ls.boardio.app.services.utils.validateToken
import pt.isel.ls.boardio.domain.users.User

class UsersServices(private val db: Database) {

    fun createUser(name: String, email: String, password: String): Pair<Int, String> {
        validateName(name) { "Invalid username" }
        validateEmail(email) { "Invalid e-mail" }
        validateString(password) { "Invalid password" }
        return db.fetch { db.users.createUser(it, name, email, password) }
    }

    fun getUserById(userId: Int): User {
        validateId(userId) { "Invalid user id" }
        return db.fetch { db.users.getUserById(it, userId) }
    }

    fun getUserByToken(token: String): User {
        validateToken(token) { "Invalid token" }
        return db.fetch { db.users.getUserByToken(it, token) }
    }

    fun loginUser(email: String, password: String): Pair<Int, String> {
        validateString(email) { "Invalid e-mail" }
        validateString(password) { "Invalid password" }
        return db.fetch { db.users.loginUser(it, email, password) }
    }

    fun getUsers(skip: Int? = null, limit: Int? = null): List<User> = db.fetch { db.users.getUsers(it, skip, limit) }

    fun getUserByName(name: String): User {
        validateName(name) { "Invalid username" }
        return db.fetch { db.users.getUserByName(it, name) }
    }

    fun deleteUser(token: String, password: String) {
        validateToken(token) { "Invalid token" }
        validateString(password) { "Invalid password" }
        return db.fetch { db.users.deleteUser(it, token, password) }
    }

    fun updateUser(token: String, password: String, name: String?, email: String?, newPassword: String?) {
        validateToken(token) { "Invalid token" }
        validateString(password) { "Invalid password" }
        if (name != null) validateName(name) { "Invalid username" }
        if (email != null) validateEmail(email) { "Invalid e-mail" }
        if (newPassword != null) validateString(newPassword) { "Invalid new password" }
        return db.fetch { db.users.updateUser(it, token, password, name, email, newPassword) }
    }
}
