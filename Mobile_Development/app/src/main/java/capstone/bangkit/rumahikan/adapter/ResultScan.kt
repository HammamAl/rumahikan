package capstone.bangkit.rumahikan.adapter

import android.os.Parcel
import android.os.Parcelable

data class ResultScan(
    val image: String? = null,
    val prediction: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(image)
        parcel.writeString(prediction)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultScan> {
        override fun createFromParcel(parcel: Parcel): ResultScan {
            return ResultScan(parcel)
        }

        override fun newArray(size: Int): Array<ResultScan?> {
            return arrayOfNulls(size)
        }
    }
}