package com.religion76.firebasechatkit.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by SunChao on 2018/8/6.
 */
class ChatSession() : Parcelable {

    var sessionId: String? = null

    //发送用户
    var fromUser: ChatUser? = null

    //接受用户
    var destUser: ChatUser? = null

    var isActive: Boolean = false

    constructor(parcel: Parcel) : this() {
        sessionId = parcel.readString()
        fromUser = parcel.readParcelable(ChatUser::class.java.classLoader)
        destUser = parcel.readParcelable(ChatUser::class.java.classLoader)
        isActive = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sessionId)
        parcel.writeParcelable(fromUser, flags)
        parcel.writeParcelable(destUser, flags)
        parcel.writeByte(if (isActive) 1 else 0)
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