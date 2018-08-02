package com.religion76.firebasechatkit

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.religion76.firebasechatkit.data.ChatMessage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val REQUEST_SIGN_IN = 0x11
    }

    private lateinit var messagesAdapter: ChatMessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLogin()

        fabSend.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                checkLogin()
                return@setOnClickListener
            }
            if (etInput.text.isNotEmpty()) {
                FirebaseDatabase.getInstance()
                        .reference
                        .push()
                        .setValue(ChatMessage(etInput.text.toString(),
                                FirebaseAuth.getInstance()?.currentUser?.displayName ?: "",
                                System.currentTimeMillis()))
                        .addOnCompleteListener {

                        }
                etInput.setText("")
            }
        }


        messagesAdapter = ChatMessagesAdapter(getFirebaseDataOptions())

        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = messagesAdapter
    }

    private fun getFirebaseDataOptions(): FirebaseRecyclerOptions<ChatMessage> {
        val query = FirebaseDatabase.getInstance()
                .reference
                .limitToLast(50)


        val options = FirebaseRecyclerOptions
                .Builder<ChatMessage>()
                .setQuery(query, ChatMessage::class.java)
                .build()

        val size = options.snapshots.size

        Log.d("FirebaseDatabase", "snapshots size:$size")

        return options
    }

    private fun checkLogin() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            Snackbar.make(containerRoot,
                    "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}",
                    Snackbar.LENGTH_LONG)
                    .show()
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    REQUEST_SIGN_IN
            )
        }
    }

    override fun onStart() {
        super.onStart()
        messagesAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        messagesAdapter.stopListening()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(containerRoot,
                        "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}",
                        Snackbar.LENGTH_LONG)
                        .show()
            } else {
                Snackbar.make(containerRoot,
                        "Sign in Failed...",
                        Snackbar.LENGTH_SHORT)
                        .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menuInflater.inflate(R.menu.menu_sign_out, it)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_sign_out)?.isVisible = FirebaseAuth.getInstance().currentUser != null
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        finish()
                        invalidateOptionsMenu()
                    }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
