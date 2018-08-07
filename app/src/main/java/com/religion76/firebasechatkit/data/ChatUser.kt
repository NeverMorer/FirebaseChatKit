package com.religion76.firebasechatkit.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by SunChao on 2018/8/6.
 */
class ChatUser() : Parcelable {
    var userId: String? = null
    var userName:String ?= null
    var hostAppUserId:String ?= null

    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        userName = parcel.readString()
        hostAppUserId = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(hostAppUserId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatUser> {
        override fun createFromParcel(parcel: Parcel): ChatUser {
            return ChatUser(parcel)
        }

        override fun newArray(size: Int): Array<ChatUser?> {
            return arrayOfNulls(size)
        }
    }


}