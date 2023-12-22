package capstone.bangkit.rumahikan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import capstone.bangkit.rumahikan.database.ListBucketDetail
import capstone.bangkit.rumahikan.repository.MainRepository
import capstone.bangkit.rumahikan.response.LoginDataAccount
import capstone.bangkit.rumahikan.response.RegisterDataAccount
import capstone.bangkit.rumahikan.response.ResponseLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val mainRepository: MainRepository) : ViewModel(){

    val buckets: LiveData<List<ListBucketDetail>> = mainRepository.buckets

    val message: LiveData<String> = mainRepository.message

    val isLoading: LiveData<Boolean> = mainRepository.isLoading

    val userlogin: LiveData<ResponseLogin> = mainRepository.userlogin

    fun login(loginDataAccount: LoginDataAccount) {
        mainRepository.getResponseLogin(loginDataAccount)
    }

    fun register(registDataUser: RegisterDataAccount) {
        mainRepository.getResponseRegister(registDataUser)
    }

    fun upload(
        photo: MultipartBody.Part,
        title: RequestBody
    ) {
        mainRepository.upload(photo, title)
    }

    @ExperimentalPagingApi
    fun getPagingBuckets(token: String): LiveData<PagingData<ListBucketDetail>> {
        return mainRepository.getPagingBuckets(token).cachedIn(viewModelScope)
    }
}