package com.example.tunetrail.data.repo

import com.example.tunetrail.data.db.User
import com.example.tunetrail.data.db.UserDao

class UserRepository(private val dao: UserDao) {
    suspend fun registerParent(name: String, email: String, password: String): Result<Long> {
        if (dao.emailExists(email)) return Result.failure(IllegalStateException("Email already used"))
        val id = dao.insert(User(name = name, email = email, password = password, role = "PARENT", parentId = null))
        return Result.success(id)
    }
    suspend fun registerKid(name: String, email: String, password: String, parentId: Long): Result<Long> {
        if (dao.emailExists(email)) return Result.failure(IllegalStateException("Email already used"))
        val id = dao.insert(User(name = name, email = email, password = password, role = "KID", parentId = parentId))
        return Result.success(id)
    }
    suspend fun login(email: String, password: String, role: String): User? {
        val u = dao.findByEmailAndRole(email, role) ?: return null
        return if (u.password == password) u else null
    }
    suspend fun kidsOf(parentId: Long) = dao.kidsForParent(parentId)
}