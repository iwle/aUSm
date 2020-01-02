package com.github.iwle.ausm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : Activity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var loginEmailEditText: EditText
    private lateinit var loginPasswordEditText: EditText
    private lateinit var loginLoginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)

        viewPager = findViewById(R.id.authenticate_view_pager)
        initialiseViewPager()

        auth = FirebaseAuth.getInstance()

        initialiseLogin()
    }

    private fun initialiseViewPager() {
        val views = listOf(R.layout.view_login, R.layout.view_signup)
        viewPager.adapter = CardPagerAdapter(views)
    }

    private fun initialiseLogin() {
        val view: View = viewPager
        loginEmailEditText = view.findViewById(R.id.email_edit_text)
        loginPasswordEditText = view.findViewById(R.id.password_edit_text)
        loginLoginButton = view.findViewById(R.id.login_button)

        loginLoginButton.setOnClickListener {
            val email: String = loginEmailEditText.text.toString()
            val password: String = loginPasswordEditText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, getString(R.string.login_field_missing), Toast.LENGTH_LONG).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, getString(R.string.login_failure), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // Hide keyboard when focus is lost
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}