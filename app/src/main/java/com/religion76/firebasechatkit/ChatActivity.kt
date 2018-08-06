package com.religion76.firebasechatkit

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.religion76.firebasechatkit.adapter.ChatMessagesAdapter
import com.religion76.firebasechatkit.data.ChatMessage
import com.religion76.firebasechatkit.data.ChatSession
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    companion object {

        val TAG = "Chaaaat"

        fun start(context: Context, chatSession: ChatSession? = null) {
            val intent = Intent(context, ChatActivity::class.java)
            chatSession?.let {
                intent.putExtra("chat_session", chatSession)
            }
            context.startActivity(intent)
        }
    }

    private var messagesAdapter: ChatMessagesAdapter? = null

    private var session: ChatSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        session = intent.getParcelableExtra("chat_session")

        if (session != null && session!!.isActive) {
            FirebaseDatabase.getInstance()
                    .reference
                    .child("session")
        }

        Log.d("ChatActivity", "session null:${session == null}")

        fabSend.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                return@setOnClickListener
            }

            if (etInput.text.isNotEmpty()) {

                val ref = FirebaseDatabase.getInstance().reference

                if (session == null) {
                    val ses = ChatUtils.createChatSession(ChatUtils.getFakeUser())
                    ses.isActive = true
                    ses.sessionId?.let { sessionId ->
                        ref.child("session")
                                .child(sessionId)
                                .setValue(ses)
                                .addOnCompleteListener {
                                    Log.d(TAG, "create session completed")
                                    session = ses
                                    refreshData(true)
                                    pushMessage()
                                    etInput.setText("")
                                }
                    }

                } else {
                    pushMessage()
                    etInput.setText("")
                }
            }
        }

        refreshData()
    }

    private fun pushMessage() {

        val sessionId = session?.sessionId ?: return
        val message = ChatMessage()
        message.content = etInput.text.toString()
        message.time = System.currentTimeMillis()
        message.sessionId = sessionId

        FirebaseDatabase.getInstance()
                .reference
                .child("message")
                .child(sessionId)
                .push()
                .setValue(message)
                .addOnCompleteListener {
                    Log.d(TAG, "push message completed")
                }
    }

    private fun refreshData(needStartListening: Boolean = false) {
        getFirebaseDataOptions()?.let { options ->
            messagesAdapter = ChatMessagesAdapter(options)
            messagesAdapter?.user = session?.fromUser
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
