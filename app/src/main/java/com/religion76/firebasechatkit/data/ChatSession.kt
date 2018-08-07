package com.religion76.firebasechatkit.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by SunChao on 2018/8/6.
 */
class ChatSession() : Parcelable {

    var sessionId: String? = null

    var userIdA: String? = null

    var userIdB: String? = null

    constructor(parcel: Parcel) : this() {
        sessionId = parcel.readString()
        userIdA = parcel.readString()
        userIdB = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sessionId)
        parcel.writeString(userIdA)
        parcel.writeString(userIdB)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatSession> {
        override fun createFromParcel(parcel: Parcel): ChatSession {
            return ChatSession(parcel)
        }

        override fun newArray(size: Int): Array<ChatSession?> {
            return arrayOfNulls(size)
        }
    }


}