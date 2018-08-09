package com.religion76.firebasechatkit.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.religion76.firebasechatkit.GlideApp
import com.religion76.firebasechatkit.R
import com.religion76.firebasechatkit.data.ChatMessage
import com.religion76.firebasechatkit.data.ChatUser
import com.religion76.firebasechatkit.ui.SinglePhotoViewActivity
import kotlinx.android.synthetic.main.item_dialog_receiver.view.*
import kotlinx.android.synthetic.main.item_dialog_sender.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by SunChao on 2018/8/2.
 */
class ChatMessagesAdapter(options: FirebaseRecyclerOptions<ChatMessage>) : FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder>(options) {

    companion object {
        private val TYPE_SENDER = 1
        private val TYPE_RECEIVER = 2
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    var myself: ChatUser? = null
    var destUser: ChatUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_SENDER) {
            return ChatSenderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_sender, parent, false))
        } else {
            return ChatReceiverViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_receiver, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).fromUserId == myself?.userId) {
            TYPE_SENDER
        } else {
            TYPE_RECEIVER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: ChatMessage) {
        if (holder is ChatSenderViewHolder) {
            holder.bind(model, sdf)
        } else if (holder is ChatReceiverViewHolder) {
            holder.bind(model, sdf)
        }
    }

    class ChatSenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: ChatMessage, sdf: SimpleDateFormat) {
            model.time?.let { time ->
                itemView.tvDateDialogSender.text = sdf.format(Date(time))
            }

            if (model.type == "photo") {
                itemView.tvContentDialogSender.visibility = View.GONE
                itemView.ivDialogPic.visibility = View.VISIBLE
                model.imageUrl?.let { url ->
                    GlideApp.with(itemView.context)
                            .load(url)
                            .placeholder(R.drawable.shape_img_placeholder)
                            .into(itemView.ivDialogPic)

                    itemView.setOnClickListener {
                        SinglePhotoViewActivity.start(itemView.context, url)
                    }
                } ?: itemView.setOnClickListener(null)
            } else {
                itemView.tvContentDialogSender.visibility = View.VISIBLE
                itemView.ivDialogPic.visibility = View.GONE
                itemView.tvContentDialogSender.text = model.content
            }
        }
    }

    class ChatReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: ChatMessage, sdf: SimpleDateFormat) {
            model.time?.let { time ->
                itemView.tvDateDialogReceiver.text = sdf.format(Date(time))
            }
            if (model.type == "photo") {
                itemView.tvContentDialogReceiver.visibility = View.GONE
                itemView.ivDialogPicReceiver.visibility = View.VISIBLE

                model.imageUrl?.let { url ->
                    GlideApp.with(itemView.context)
                            .load(url)
                            .placeholder(R.drawable.shape_img_placeholder)
                            .into(itemView.ivDialogPicReceiver)
                    itemView.setOnClickListener {
                        SinglePhotoViewActivity.start(itemView.context, url)
                    }
                } ?: itemView.setOnClickListener(null)
            } else {
                itemView.tvContentDialogReceiver.visibility = View.VISIBLE
                itemView.ivDialogPicReceiver.visibility = View.GONE
                itemView.tvContentDialogReceiver.text = model.content
            }
        }
    }
}