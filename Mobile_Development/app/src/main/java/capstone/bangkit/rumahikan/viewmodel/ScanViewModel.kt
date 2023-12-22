package capstone.bangkit.rumahikan.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import capstone.bangkit.rumahikan.R
import capstone.bangkit.rumahikan.api.ApiConfig
import capstone.bangkit.rumahikan.response.ResponseDisease
import capstone.bangkit.rumahikan.ui.ScanActivity
import capstone.bangkit.rumahikan.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ScanViewModel(val context: Context) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _resultPage = MutableLiveData<Boolean>()

    var error = MutableLiveData("")

    private val _diseaseResult = MutableLiveData<String>()

    private val _message = MutableLiveData<String>()

    private val _isLoading = MutableLiveData<Boolean>()

    private val _result = MutableLiveData<ResponseDisease>()
    val result: LiveData<ResponseDisease> = _result

    fun uploadImage() {
        if (ScanActivity.getFile != null) {
            val image = reduceFileImage(ScanActivity.getFile as File)
            val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                image.name,
                requestImageFile
            )

            Log.d(TAG, "Upload image started")

            _loading.value = true
            _resultPage.value = false

            val client = ApiConfig.getApiServiceDetect().uploadImage(imageMultipart)
            client.enqueue(object : Callback<ResponseDisease> {
                override fun onResponse(
                    call: Call<ResponseDisease>,
                    response: Response<ResponseDisease>
                ) {
                    _isLoading.postValue(false)
                    Log.d(TAG, "Received response from server")

                    if (response.isSuccessful) {
                        _result.postValue(response.body())
                        _resultPage.postValue(true)

                        Log.d(TAG, "Disease Prediction: ${response.body()?.prediction ?: "No result"}")

                        _diseaseResult.postValue(response.body()?.prediction ?: "No result")
                    } else {
                        error.postValue("Error ${response.code()} : ${response.message()}")
                        _resultPage.postValue(false)
                    }
                    _loading.postValue(false)
                }

                override fun onFailure(call: Call<ResponseDisease>, t: Throwable) {
                    _loading.postValue(false)
                    _resultPage.postValue(false)
                    Log.e(TAG, "onFailure Call: ${t.message}")
                    error.postValue("${context.getString(R.string.error_upload)} : ${t.message}")
                }

            })
        }
    }
    companion object {
        const val TAG = "ScanViewModel"
    }
}