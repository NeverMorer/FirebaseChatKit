package com.religion76.firebasechatkit.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.religion76.firebasechatkit.ChatManager
import com.religion76.firebasechatkit.R
import com.religion76.firebasechatkit.adapter.ChatMessagesAdapter
import com.religion76.firebasechatkit.data.ChatMessage
import com.religion76.firebasechatkit.data.ChatUser
import com.religion76.firebasechatkit.data.UserSession
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : ImageSelectorActivity() {

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

    private lateinit var chatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val myself = intent.getParcelableExtra<ChatUser>("myself")
        val destUser = intent.getParcelableExtra<ChatUser>("dest_user")

        if (myself == null || destUser == null) {
            finish()
            return
        }

        chatManager = ChatManager(myself, destUser)

        title = destUser.userName
        session = intent.getParcelableExtra("user_session")

        fabSend.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null && etInput.text.isNotEmpty()) {
                if (session == null) {
                    chatManager.createChatSession { userSession ->
                        this.session = userSession
                        refreshData(chatManager.getMyself(), chatManager.getDestUser(), true)

                        userSession.sessionId?.let { sessionId ->
                            chatManager.pushMessage(sessionId, etInput.text.toString())
                        }
                        etInput.setText("")
                    }
                } else {
                    session!!.sessionId?.let { sessionId ->
                        chatManager.pushMessage(sessionId, etInput.text.toString())
                    }
                    etInput.setText("")
                }
            }
        }

        ivAddPhoto.setOnClickListener {
            selectImage { path ->
                Log.d(TAG, "file_path:$path")
            }
        }

        refreshData(chatManager.getMyself(), chatManager.getDestUser())

    }

    private fun refreshData(myself: ChatUser, destUser: ChatUser, needStartListening: Boolean = false) {
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
