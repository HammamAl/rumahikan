package capstone.bangkit.rumahikan.api

import capstone.bangkit.rumahikan.*
import capstone.bangkit.rumahikan.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("signup")
    fun registUser(@Body requestRegister: RegisterDataAccount): Call<ResponseDetail>

    @POST("login")
    fun loginUser(@Body requestLogin: LoginDataAccount): Call<ResponseLogin>

    @GET("buckets")
    suspend fun getPagingBucket(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Header("Authorization") token: String,
    ): ResponsePagingBucket

    @Multipart
    @POST("buckets")
    fun addBucket(
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody
    ): Call<ResponseDetail>
}

interface ApiServiceUpload {
    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ResponseDisease>
}