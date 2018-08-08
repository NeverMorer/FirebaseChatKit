package com.religion76.firebasechatkit

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.religion76.firebasechatkit.data.ChatMessage
import com.religion76.firebasechatkit.data.ChatUser
import com.religion76.firebasechatkit.data.UserSession
import com.religion76.firebasechatkit.ui.ChatActivity

/**
 * Created by SunChao on 2018/8/8.
 */
class ChatManager(private val myself: ChatUser, private val destUser: ChatUser) {

    fun getMyself() = myself
    fun getDestUser() = destUser

    fun createChatSession(onSucceed: ((userSession: UserSession) -> Unit)? = null) {
        if (myself.userId != null && destUser.userId != null) {
            val ses = ChatUtils.createChatSession(myself.userId!!, destUser.userId!!)
            ses.sessionId?.let { sessionId ->
                FirebaseDatabase.getInstance()
                        .reference
                        .child("chat_session")
                        .child(sessionId)
                        .setValue(ses)
                        .addOnCompleteListener {
                            Log.d(ChatActivity.TAG, "create chat session completed")
                            ses.sessionId?.let { sessionId ->
                                createUserSession(sessionId) {
                                    onSucceed?.invoke(it)
                                }
                            }
                        }
            }
        }
    }

    private fun createUserSession(chatSessionId: String, onSucceed: ((userSession: UserSession) -> Unit)?) {

        if (myself.userId == null || destUser.userId == null) {
            return
        }

        val mySession = UserSession()
        mySession.sessionId = chatSessionId
        mySession.fromUserId = myself.userId
        mySession.destUser = destUser

        val destUserSession = UserSession()
        destUserSession.sessionId = chatSessionId
        destUserSession.fromUserId = destUser.userId
        destUserSession.destUser = myself

        val userSessions = HashMap<String, UserSession>()

        userSessions[myself.userId!!] = mySession
        userSessions[destUser.userId!!] = destUserSession

        val ref = FirebaseDatabase.getInstance()
                .reference
                .child("user_session")

        var sessionCreatedCount = 0

        ref.child(myself.userId!!)
                .child(destUser.userId!!)
                .setValue(mySession)
                .addOnCompleteListener {
                    Log.d(ChatActivity.TAG, "create user's session completed")
//                    session = mySession
                    sessionCreatedCount++
                    if (sessionCreatedCount == 2) {
                        onSucceed?.invoke(mySession)
                    }
                }

        ref.child(destUser.userId!!)
                .child(myself.userId!!)
                .setValue(destUserSession)
                .addOnCompleteListener {
                    Log.d(ChatActivity.TAG, "create user's session completed")
                    sessionCreatedCount++
                    if (sessionCreatedCount == 2) {
                        onSucceed?.invoke(mySession)
                    }
                }
    }

    fun pushMessage(sessionId: String, messageText: String) {

        val message = ChatMessage()
        message.content = messageText
        message.time = System.currentTimeMillis()
        message.sessionId = sessionId
        message.fromUserId = myself.userId

        pushMessage(sessionId, message)
    }

    fun pushMessageWithPic(sessionId: String, imageUrl: String) {
        val message = ChatMessage()
        message.imageUrl = imageUrl
        message.time = System.currentTimeMillis()
        message.sessionId = sessionId
        message.fromUserId = myself.userId

        pushMessage(sessionId, message)
    }

    private fun pushMessage(sessionId: String, message: ChatMessage) {
        FirebaseDatabase.getInstance()
                .reference
                .child("message")
                .child(sessionId)
                .push()
                .setValue(message)
                .addOnCompleteListener {
                    Log.d(ChatActivity.TAG, "push message completed")
                    updateSession(message)
                }
    }

    private fun updateSession(message: ChatMessage) {
        val userSessionsUpdates = HashMap<String, Any?>()

        val msg = message.content ?: "[pic]"

        userSessionsUpdates["${myself.userId}/${destUser.userId}/lastMessage"] = msg
        userSessionsUpdates["${myself.userId}/${destUser.userId}/lastTime"] = message.time
        userSessionsUpdates["${myself.userId}/${destUser.userId}/destUser"] = destUser

        userSessionsUpdates["${destUser.userId}/${myself.userId}/lastMessage"] = msg
        userSessionsUpdates["${destUser.userId}/${myself.userId}/lastTime"] = message.time
        userSessionsUpdates["${destUser.userId}/${myself.userId}/destUser"] = myself
        userSessionsUpdates["${destUser.userId}/${myself.userId}/isActive"] = true

        FirebaseDatabase.getInstance()
                .reference
                .child("user_session")
                .updateChildren(userSessionsUpdates)
    }

}