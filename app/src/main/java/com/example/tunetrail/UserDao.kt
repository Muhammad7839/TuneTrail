package com.example.tunetrail.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email AND role = :role LIMIT 1")
    suspend fun findByEmailAndRole(email: String, role: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun emailExists(email: String): Boolean

    @Query("SELECT * FROM users WHERE parentId = :parentId AND role = 'KID'")
    suspend fun kidsForParent(parentId: Long): List<User>
}