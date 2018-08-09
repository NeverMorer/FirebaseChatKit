package com.religion76.firebasechatkit.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.religion76.firebasechatkit.GlideApp
import com.religion76.firebasechatkit.R
import kotlinx.android.synthetic.main.activity_single_photo_view.*

class SinglePhotoViewActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, url: String) {
            val intent = Intent(context, SinglePhotoViewActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_photo_view)

        val url = intent.getStringExtra("url")
        if (url.isNullOrEmpty()) {
            finish()
            return
        }

        GlideApp.with(this)
                .load(Uri.parse(url))
                .into(photoView)
    }
}
