package com.religion76.firebasechatkit.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.religion76.firebasechatkit.ui.ChatActivity
import com.religion76.firebasechatkit.R
import com.religion76.firebasechatkit.data.ChatUser
import com.religion76.firebasechatkit.data.UserSession
import kotlinx.android.synthetic.main.item_chat_session.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by SunChao on 2018/8/2.
 */
class ChatSessionsAdapter(options: FirebaseRecyclerOptions<UserSession>) : FirebaseRecyclerAdapter<UserSession, ChatSessionsAdapter.ChatSessionViewHolder>(options) {

    val sdf by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    }

    var myself: ChatUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatSessionViewHolder {
        return ChatSessionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_session, parent, false))
    }

    override fun onBindViewHolder(holder: ChatSessionViewHolder, position: Int, model: UserSession) {

        val destUser =  model.destUser

        holder.itemView.tvUserName.text = destUser?.userName
        holder.itemView.tvLastMsg.text = model.lastMessage
        holder.itemView.ivSessionState.visibility = if (model.isActive) View.VISIBLE else View.INVISIBLE
        model.lastTime?.let {
            holder.itemView.tvLastTime.text = sdf.format(it)
        }
        holder.itemView.setOnClickListener {
            if (myself != null && destUser != null) {
                ChatActivity.start(holder.itemView.context, myself!!, destUser, model)
            }
        }
    }

    class ChatSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}