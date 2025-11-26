package com.example.tunetrail.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionStore(private val context: Context) {
    companion object {
        private val USER_ID = longPreferencesKey("user_id")
        private val ROLE = stringPreferencesKey("role")
    }
    suspend fun set(userId: Long, role: String) {
        context.sessionDataStore.edit { it[USER_ID] = userId; it[ROLE] = role }
    }
    suspend fun clear() { context.sessionDataStore.edit { it.clear() } }
    val flow: Flow<Pair<Long?, String?>> =
        context.sessionDataStore.data.map { prefs -> prefs[USER_ID] to prefs[ROLE] }
}