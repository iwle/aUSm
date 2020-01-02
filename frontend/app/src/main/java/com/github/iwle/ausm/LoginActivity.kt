package com.github.iwle.ausm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : Activity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, getString(R.string.login_field_missing), Toast.LENGTH_LONG).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
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