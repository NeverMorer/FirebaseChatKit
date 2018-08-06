package com.religion76.firebasechatkit

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.religion76.firebasechatkit.adapter.ChatSessionsAdapter
import com.religion76.firebasechatkit.data.ChatSession
import kotlinx.android.synthetic.main.activity_chat_sessions.*

class ChatSessionsActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChatSessionsActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var sessionsAdapter: ChatSessionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_sessions)


        sessionsAdapter = ChatSessionsAdapter(getSessionDataOptions())
        rvSessions.layoutManager = LinearLayoutManager(this)
        rvSessions.adapter = sessionsAdapter
    }

    private fun getSessionDataOptions(): FirebaseRecyclerOptions<ChatSession> {
        val query = FirebaseDatabase.getInstance()
                .reference
                .child("session")

        return FirebaseRecyclerOptions.Builder<ChatSession>()
                .setQuery(query, ChatSession::class.java)
                .build()

    }


    override fun onStart() {
        super.onStart()
        sessionsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        sessionsAdapter.stopListening()
    }
}
