package com.religion76.firebasechatkit

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val REQUEST_SIGN_IN = 0x11
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUserEntrance.setOnClickListener {
            ChatActivity.start(this)
        }

        btnStaffEntrance.setOnClickListener {
            ChatSessionsActivity.start(this)
        }

        checkLogin()
    }

    private fun checkLogin() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            loginSecceed()
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

    private fun loginSecceed(){
        btnUserEntrance.visibility = View.VISIBLE
        btnStaffEntrance.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                loginSecceed()
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
