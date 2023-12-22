package capstone.bangkit.rumahikan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import capstone.bangkit.rumahikan.preference.UserPreferences
import kotlinx.coroutines.launch

class DataStoreViewModel(private val pref: UserPreferences) : ViewModel() {

    fun getLoginSession(): LiveData<Boolean> {
        return pref.getLoginSession().asLiveData()
    }

    fun saveLoginSession(loginSession: Boolean) {
        viewModelScope.launch {
            pref.saveLoginSession(loginSession)
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun getName(): LiveData<String> {
        return pref.getName().asLiveData()
    }

    fun saveName(token: String) {
        viewModelScope.launch {
            pref.saveName(token)
        }
    }

    fun observeLoginSession(): LiveData<Boolean> {
        return pref.getLoginSession().asLiveData()
    }

    fun clearDataLogin() {
        viewModelScope.launch {
            pref.clearDataLogin()
        }
    }
}