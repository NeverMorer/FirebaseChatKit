package com.religion76.firebasechatkit.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.religion76.firebasechatkit.ChatActivity
import com.religion76.firebasechatkit.R
import com.religion76.firebasechatkit.data.ChatSession
import kotlinx.android.synthetic.main.item_chat_session.view.*

/**
 * Created by SunChao on 2018/8/2.
 */
class ChatSessionsAdapter(options: FirebaseRecyclerOptions<ChatSession>) : FirebaseRecyclerAdapter<ChatSession, ChatSessionsAdapter.ChatSessionViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatSessionViewHolder {
        return ChatSessionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_session, parent, false))
    }

    override fun onBindViewHolder(holder: ChatSessionViewHolder, position: Int, model: ChatSession) {

        holder.itemView.tvUserName.text = model.fromUser?.userName
        holder.itemView.ivSessionState.visibility = if (model.isActive) View.VISIBLE else View.INVISIBLE
        holder.itemView.setOnClickListener {
            ChatActivity.start(holder.itemView.context, model)
        }
    }

    class ChatSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}