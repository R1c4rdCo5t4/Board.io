package pt.isel.ls.boardio.domain.users.database

import pt.isel.ls.boardio.app.database.source.Source
import pt.isel.ls.boardio.domain.users.User

interface UsersSource {
    fun createUser(source: Source, name: String, email: String, password: String): Pair<Int, String>
    fun getUserById(source: Source, userId: Int): User
    fun getUserByToken(source: Source, token: String): User
    fun loginUser(source: Source, email: String, password: String): Pair<Int, String>
    fun getUsers(source: Source, skip: Int? = null, limit: Int? = null): List<User>
    fun getUserByName(source: Source, name: String): User
    fun updateUser(source: Source, token: String, password: String, name: String? = null, email: String? = null, newPassword: String? = null)
    fun deleteUser(source: Source, token: String, password: String)
}
