package com.example.tunetrail.di

import android.content.Context
import com.example.tunetrail.data.db.AppDatabase
import com.example.tunetrail.data.repo.UserRepository
import com.example.tunetrail.data.session.SessionStore

object AppGraph {
    lateinit var db: AppDatabase
        private set
    lateinit var userRepo: UserRepository
        private set
    lateinit var session: SessionStore
        private set

    fun init(context: Context) {
        db = AppDatabase.get(context)
        userRepo = UserRepository(db.userDao())
        session = SessionStore(context)
    }
}