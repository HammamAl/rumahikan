package capstone.bangkit.rumahikan.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getLoginSession(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_SESSION] ?: false
        }
    }

    suspend fun saveLoginSession(loginSession: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_SESSION] = loginSession
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }


    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[NAME] ?: ""
        }
    }


    suspend fun saveName(name: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }

    suspend fun clearDataLogin() {
        dataStore.edit { preferences ->
            preferences.remove(LOGIN_SESSION)
            preferences.remove(TOKEN)
            preferences.remove(NAME)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val LOGIN_SESSION = booleanPreferencesKey("login_session")
        private val TOKEN = stringPreferencesKey("token")
        private val NAME = stringPreferencesKey("name")

    }
}