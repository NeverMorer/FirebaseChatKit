package com.religion76.firebasechatkit.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by SunChao on 2018/8/7.
 */
class UserSession() : Parcelable {
    var sessionId: String? = null

    var fromUserId: String? = null

    var destUser: ChatUser? = null

    var isActive: Boolean = false

    var lastMessageType: String? = null

    var lastMessage: String? = null

    var lastTime: Long? = null

    constructor(parcel: Parcel) : this() {
        sessionId = parcel.readString()
        fromUserId = parcel.readString()
        destUser = parcel.readParcelable(ChatUser::class.java.classLoader)
        isActive = parcel.readByte() != 0.toByte()
        lastMessageType = parcel.readString()
        lastMessage = parcel.readString()
        lastTime = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sessionId)
        parcel.writeString(fromUserId)
        parcel.writeParcelable(destUser, flags)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeString(lastMessageType)
        parcel.writeString(lastMessage)
        parcel.writeValue(lastTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserSession> {
        override fun createFromParcel(parcel: Parcel): UserSession {
            return UserSession(parcel)
        }

        override fun newArray(size: Int): Array<UserSession?> {
            return arrayOfNulls(size)
        }
    }
}