package com.religion76.firebasechatkit

import com.religion76.firebasechatkit.data.ChatSession
import com.religion76.firebasechatkit.data.ChatUser
import java.util.*

/**
 * Created by SunChao on 2018/8/6.
 */
object ChatUtils {
    fun createChatSession(user: ChatUser): ChatSession {
        val staffUser = ChatUser()
        staffUser.userId = "10000"
        staffUser.userName = "Admin"

        val chatSession = ChatSession()

        chatSession.sessionId = UUID.randomUUID().toString()
        chatSession.destUser = staffUser
        chatSession.fromUser = user

        return chatSession
    }
}