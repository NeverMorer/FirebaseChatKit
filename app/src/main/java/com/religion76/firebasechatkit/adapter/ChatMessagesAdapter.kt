package com.religion76.firebasechatkit.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.religion76.firebasechatkit.R
import com.religion76.firebasechatkit.data.ChatMessage
import com.religion76.firebasechatkit.data.ChatUser
import kotlinx.android.synthetic.main.item_chat_message.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by SunChao on 2018/8/2.
 */
class ChatMessagesAdapter(options: FirebaseRecyclerOptions<ChatMessage>) : FirebaseRecyclerAdapter<ChatMessage, ChatMessagesAdapter.ChatMessageViewHolder>(options) {
    val sdf by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    }

    var myself: ChatUser? = null
    var destUser: ChatUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        return ChatMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false))
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int, model: ChatMessage) {
        holder.itemView.tvUserName.text = if (model.fromUserId == myself?.userId) myself?.userName else destUser?.userName
        holder.itemView.tvMessage.text = model.content
        model.time?.let { time ->
            holder.itemView.tvTime.text = sdf.format(Date(time))
        }
    }

    class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}