package capstone.bangkit.rumahikan.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import capstone.bangkit.rumahikan.api.ApiConfig
import capstone.bangkit.rumahikan.api.ApiService
import capstone.bangkit.rumahikan.database.BucketDatabase
import capstone.bangkit.rumahikan.database.ListBucketDetail
import capstone.bangkit.rumahikan.espresso.wrapEspressoIdlingResource
import capstone.bangkit.rumahikan.response.LoginDataAccount
import capstone.bangkit.rumahikan.response.RegisterDataAccount
import capstone.bangkit.rumahikan.response.ResponseDetail
import capstone.bangkit.rumahikan.response.ResponseLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(
    private val bucketDatabase: BucketDatabase,
    private val apiService: ApiService,
) {

    private var _buckets = MutableLiveData<List<ListBucketDetail>>()
    var buckets: LiveData<List<ListBucketDetail>> = _buckets

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userLogin = MutableLiveData<ResponseLogin>()
    var userlogin: LiveData<ResponseLogin> = _userLogin

    fun getResponseLogin(loginDataAccount: LoginDataAccount) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val api = ApiConfig.getApiService().loginUser(loginDataAccount)
            api.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    _isLoading.value = false
                    val responseBody = response.body()

                    if (response.isSuccessful) {
                        _userLogin.value = responseBody!!
                        _message.value = "Hello ${_userLogin.value!!.loginResult.name}!"

                    } else {
                        when (response.code()) {
                            401 -> _message.value =
                                "Email or password that you are input are wrong, please try again"
                            408 -> _message.value =
                                "Your connection is low, please try again"
                            else -> _message.value = "Error message: " + response.message()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = "Error message: " + t.message.toString()
                }

            })
        }
    }

    fun getResponseRegister(registDataUser: RegisterDataAccount) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val api = ApiConfig.getApiService().registUser(registDataUser)
            api.enqueue(object : Callback<ResponseDetail> {
                override fun onResponse(
                    call: Call<ResponseDetail>,
                    response: Response<ResponseDetail>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _message.value = "Account successfully created"
                    } else {
                        when (response.code()) {
                            400 -> _message.value =
                                "Email has already registered, please try again"
                            408 -> _message.value =
                                "Your connection is low, please try again"
                            else -> _message.value = "Error message: " + response.message()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = "Error message: " + t.message.toString()
                }

            })
        }
    }

    fun upload(
        photo: MultipartBody.Part,
        title: RequestBody
    ) {
        _isLoading.value = true
        val service = ApiConfig.getApiService().addBucket(
            photo, title
        )
        service.enqueue(object : Callback<ResponseDetail> {
            override fun onResponse(
                call: Call<ResponseDetail>,
                response: Response<ResponseDetail>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _message.value = responseBody.message
                    }
                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                _isLoading.value = false
                _message.value = t.message
            }
        })
    }

    @ExperimentalPagingApi
    fun getPagingBuckets(token: String): LiveData<PagingData<ListBucketDetail>> {
        @OptIn(ExperimentalPagingApi::class)
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = BucketRemoteMediator(bucketDatabase, apiService, token),
            pagingSourceFactory = {
                bucketDatabase.getListBucketDetailDao().getAllBuckets()
            }
        )
        return pager.liveData
    }
}