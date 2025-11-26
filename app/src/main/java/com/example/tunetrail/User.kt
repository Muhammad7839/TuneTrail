package com.example.tunetrail.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val password: String, // simple for class project
    val role: String,     // "PARENT" or "KID"
    val parentId: Long?   // kids point to parentId; parents null
)