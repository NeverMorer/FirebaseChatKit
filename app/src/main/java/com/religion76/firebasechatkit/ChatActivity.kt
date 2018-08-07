package com.religion76.firebasechatkit

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.religion76.firebasechatkit.adapter.ChatMessagesAdapter
import com.religion76.firebasechatkit.data.ChatMessage
import com.religion76.firebasechatkit.data.ChatUser
import com.religion76.firebasechatkit.data.UserSession
import kotlinx.android.synthetic.main.activity_chat.*
import kotlin.collections.HashMap


class ChatActivity : AppCompatActivity() {

    companion object {

        val TAG = "Chaaaat"

        fun start(context: Context, myself: ChatUser, destUser: ChatUser, userSession: UserSession? = null) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("myself", myself)
            intent.putExtra("dest_user", destUser)
            userSession?.let {
                intent.putExtra("user_session", userSession)
            }
            context.startActivity(intent)
        }
    }

    private var messagesAdapter: ChatMessagesAdapter? = null

    private var session: UserSession? = null

    private lateinit var myself: ChatUser

    private lateinit var destUser: ChatUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val myself = intent.getParcelableExtra<ChatUser>("myself")
        val destUser = intent.getParcelableExtra<ChatUser>("dest_user")

        if (myself == null || destUser == null) {
            finish()
            return
        }

        title = destUser.userName

        session = intent.getParcelableExtra("user_session")

        fabSend.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                return@setOnClickListener
            }

            if (etInput.text.isNotEmpty()) {
                if (session == null && myself.userId != null && destUser.userId != null) {
                    createChatSession {
                        refreshData(true)
                        pushMessage()
                        etInput.setText("")
                    }
                } else {
                    pushMessage()
                    etInput.setText("")
                }
            }
        }

        this.myself = myself
        this.destUser = destUser
        refreshData()
    }

    private fun createChatSession(onSucceed: (() -> Unit)? = null) {
        val ses = ChatUtils.createChatSession(myself.userId!!, destUser.userId!!)
        pbLoading.visibility = View.VISIBLE
        ses.sessionId?.let { sessionId ->
            FirebaseDatabase.getInstance()
                    .reference
                    .child("chat_session")
                    .child(sessionId)
                    .setValue(ses)
                    .addOnCompleteListener {
                        Log.d(TAG, "create chat session completed")
                        ses.sessionId?.let { sessionId ->
                            createUserSession(sessionId, myself, destUser) {
                                pbLoading.visibility = View.GONE
                                onSucceed?.invoke()
                            }
                        }
                    }
        }
    }

    private fun createUserSession(chatSessionId: String, myself: ChatUser, destUser: ChatUser, onSucceed: (() -> Unit)?) {

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
                    Log.d(TAG, "create user's session completed")
                    session = mySession
                    sessionCreatedCount++
                    if (sessionCreatedCount == 2) {
                        onSucceed?.invoke()
                    }
                }

        ref.child(destUser.userId!!)
                .child(myself.userId!!)
                .setValue(destUserSession)
                .addOnCompleteListener {
                    Log.d(TAG, "create user's session completed")
                    sessionCreatedCount++
                    if (sessionCreatedCount == 2) {
                        onSucceed?.invoke()
                    }
                }
    }

    private fun getDestUserById(userId: String) {

        pbLoading.visibility = View.VISIBLE

        FirebaseDatabase.getInstance()
                .reference
                .child("user")
                .child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        pbLoading.visibility = View.GONE
                    }

                    override fun onDataChange(data: DataSnapshot) {
                        pbLoading.visibility = View.GONE
                        if (data.exists()) {
                            data.getValue(ChatUser::class.java)?.let {
                                destUser = it
                                refreshData()
                            }
                                    ?: Snackbar.make(containerRoot, "user data error", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(containerRoot, "can't find the user", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                })

    }

    private fun pushMessage() {

        val sessionId = session?.sessionId ?: return
        val message = ChatMessage()
        message.content = etInput.text.toString()
        message.time = System.currentTimeMillis()
        message.sessionId = sessionId
        message.fromUserId = myself.userId

        FirebaseDatabase.getInstance()
                .reference
                .child("message")
                .child(sessionId)
                .push()
                .setValue(message)
                .addOnCompleteListener {
                    Log.d(TAG, "push message completed")
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

    private fun refreshData(needStartListening: Boolean = false) {
        getFirebaseDataOptions()?.let { options ->
            messagesAdapter = ChatMessagesAdapter(options)
            messagesAdapter?.myself = myself
            messagesAdapter?.destUser = destUser
            rvMessages.layoutManager = LinearLayoutManager(this)
            rvMessages.adapter = messagesAdapter

            if (needStartListening) {
                messagesAdapter?.startListening()
            }
        }
    }

    private fun getFirebaseDataOptions(): FirebaseRecyclerOptions<ChatMessage>? {

        session?.sessionId?.let { sessionId ->
            val query = FirebaseDatabase.getInstance()
                    .reference
                    .child("message")
                    .child(sessionId)

            return FirebaseRecyclerOptions
                    .Builder<ChatMessage>()
                    .setQuery(query, ChatMessage::class.java)
                    .build()
        }

        return null
    }

    override fun onStart() {
        super.onStart()
        messagesAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        messagesAdapter?.stopListening()
    }

}
