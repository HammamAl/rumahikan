package capstone.bangkit.rumahikan.response

import capstone.bangkit.rumahikan.database.ListBucketDetail
import com.google.gson.annotations.SerializedName

data class RegisterDataAccount(
    var name: String,
    var email: String,
    var password: String
)

data class LoginDataAccount(
    var email: String,
    var password: String
)

data class ResponseDetail(
    var error: Boolean,
    var message: String
)

data class ResponseLogin(
    var error: Boolean,
    var message: String,
    var loginResult: LoginResult
)

data class LoginResult(
    var userId: String,
    var name: String,
    var token: String
)

data class ResponsePagingBucket(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listBucket")
    var listBucket: List<ListBucketDetail>
)

data class ResponseDisease(
    var prediction: String
)