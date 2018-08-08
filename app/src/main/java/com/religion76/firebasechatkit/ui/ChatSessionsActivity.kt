package com.religion76.firebasechatkit.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.religion76.firebasechatkit.R
import com.religion76.firebasechatkit.adapter.ChatSessionsAdapter
import com.religion76.firebasechatkit.data.ChatUser
import com.religion76.firebasechatkit.data.UserSession
import kotlinx.android.synthetic.main.activity_chat_sessions.*

class ChatSessionsActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, myself: ChatUser) {
            val intent = Intent(context, ChatSessionsActivity::class.java)
            intent.putExtra("myself", myself)
            context.startActivity(intent)
        }
    }

    private lateinit var sessionsAdapter: ChatSessionsAdapter

    private lateinit var myself: ChatUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_sessions)

        val parcelableExtra = intent.getParcelableExtra<ChatUser>("myself")
        if (parcelableExtra == null) {
            finish()
            return
        }

        myself = parcelableExtra

        getSessionDataOptions()?.let { options ->
            sessionsAdapter = ChatSessionsAdapter(options)
            sessionsAdapter.myself = myself
            rvSessions.layoutManager = LinearLayoutManager(this)
            rvSessions.adapter = sessionsAdapter
        } ?: finish()
    }

    private fun getSessionDataOptions(): FirebaseRecyclerOptions<UserSession>? {

        myself.userId?.let { userId ->
            val query = FirebaseDatabase.getInstance()
                    .reference
                    .child("user_session")
                    .child(userId)

            return FirebaseRecyclerOptions.Builder<UserSession>()
                    .setQuery(query, UserSession::class.java)
                    .build()
        }
        return null
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
