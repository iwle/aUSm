package com.github.iwle.ausm

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgetPasswordTextView: TextView
    private lateinit var signupTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_login, container, false)

        val viewPagerId = arguments!!.getInt("viewPagerId")
        viewPager = activity!!.findViewById(viewPagerId)

        emailEditText = v.findViewById(R.id.email_edit_text)
        passwordEditText = v.findViewById(R.id.password_edit_text)
        loginButton = v.findViewById(R.id.login_button)
        forgetPasswordTextView = v.findViewById(R.id.forget_password_text_view)
        signupTextView = v.findViewById(R.id.signup_text_view)
        firebaseAuth = FirebaseAuth.getInstance()

        initialiseLogin()
        initialiseForgetPassword()
        initialiseSignUp()

        return v
    }

    private fun initialiseLogin() {
        loginButton.setOnClickListener {
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this.activity, getString(R.string.field_missing), Toast.LENGTH_LONG).show()
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this.activity as Activity) { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this.activity, getString(R.string.login_success), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this.activity, getString(R.string.login_failure), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initialiseForgetPassword() {
        forgetPasswordTextView.setOnClickListener {
            val email: String = emailEditText.text.toString()

            if(TextUtils.isEmpty(email)) {
                Toast.makeText(this.activity, getString(R.string.email_missing), Toast.LENGTH_LONG).show()
            } else {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this.activity as Activity) { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this.activity, getString(R.string.email_reset_success), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this.activity, getString(R.string.email_reset_failure), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initialiseSignUp() {
        signupTextView.setOnClickListener {
            viewPager.currentItem++
        }
    }
}