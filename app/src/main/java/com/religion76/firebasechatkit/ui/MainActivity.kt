package com.religion76.firebasechatkit.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.religion76.firebasechatkit.ChatUtils
import com.religion76.firebasechatkit.R
import com.religion76.firebasechatkit.data.ChatUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val REQUEST_SIGN_IN = 0x11
    }

    private var myself: ChatUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUserEntrance.setOnClickListener {
            enterChatWithFakeUser()
        }

        btnStaffEntrance.setOnClickListener {
            if (myself != null){
                Log.d("zzz", "myself_id${myself?.userId} myself_name${myself?.userName}  hostAppUserId${myself?.hostAppUserId}")
                ChatSessionsActivity.start(this, myself!!)
            }
        }

        checkLogin()
    }

    private fun checkLogin() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            loginSucceed()
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

    private fun loginSucceed() {
        btnUserEntrance.visibility = View.VISIBLE
        btnStaffEntrance.visibility = View.VISIBLE

        val hostAppUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase
                .getInstance()
                .reference
                .child("user")
                .orderByChild("hostAppUserId")
                .equalTo(hostAppUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(data: DataSnapshot) {
                        if (data.exists()) {
                            Log.d("zzz", "user snapshot childCount:${data.childrenCount}")
                            myself = data.children.first().getValue(ChatUser::class.java)
                            Snackbar.make(containerRoot,
                                    "Welcome Back!",
                                    Snackbar.LENGTH_LONG)
                                    .show()
                        } else {
                            val fakeUser = ChatUtils.getFakeUser()
                            fakeUser.hostAppUserId = FirebaseAuth.getInstance().currentUser?.uid
                            registerChatUser(fakeUser) {
                                myself = fakeUser
                            }
                        }
                    }
                })

    }

    private fun registerChatUser(user: ChatUser, onSucceed: (() -> Unit)? = null) {
        FirebaseDatabase
                .getInstance()
                .reference
                .child("user")
                .child(user.userId!!)
                .setValue(user)
                .addOnCompleteListener {
                    onSucceed?.invoke()
                }
    }

    private fun enterChatWithFakeUser() {
        if (myself != null) {
            val fakeUser = ChatUtils.getFakeUser()
            registerChatUser(fakeUser) {
                Snackbar.make(containerRoot,
                        "Create Faker Succeed",
                        Snackbar.LENGTH_LONG)
                        .show()
                ChatActivity.start(this, fakeUser, myself!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                loginSucceed()
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
        menu?.findItem(R.id.menu_clear_data)?.isVisible = FirebaseAuth.getInstance().currentUser != null
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
        } else if (item?.itemId == R.id.menu_clear_data) {
            FirebaseDatabase.getInstance().reference.removeValue().addOnCompleteListener {
                Log.d(ChatActivity.TAG, "remove session completed")
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
