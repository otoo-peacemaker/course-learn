package com.peacemaker.android.courselearn.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.USER_PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharedPreferences(context: Context) {
    private val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )
    private val dataStore: DataStore<Preferences> = context.dataStore

    // Functions to store and retrieve values
    suspend fun saveStringValue(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun saveIntValue(key: String, value: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    suspend fun saveBooleanValue(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    fun getStringValue(key: String): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }
    }

    fun getIntValue(key: String): Flow<Int?> {
        return dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)]
        }
    }

    fun getBooleanValue(key: String): Flow<Boolean?> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)]
        }
    }
}


//val customDataStore = CustomDataStore(context)
//
//// Save values
//customDataStore.saveStringValue("username", "john_doe")
//customDataStore.saveIntValue("age", 25)
//customDataStore.saveBooleanValue("is_logged_in", true)
//
//// Retrieve values using flows
//customDataStore.getStringValue("username").observe(this) { username ->
//    // Do something with the username
//}