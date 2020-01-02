package com.github.iwle.ausm

import android.app.Activity
import android.content.Intent
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

class SignupFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_signup, container, false)

        val viewPagerId = arguments!!.getInt("viewPagerId")
        viewPager = activity!!.findViewById(viewPagerId)

        firstName = v.findViewById(R.id.first_name_edit_text)
        lastName = v.findViewById(R.id.last_name_edit_text)
        emailEditText = v.findViewById(R.id.email_edit_text)
        passwordEditText = v.findViewById(R.id.password_edit_text)
        signupButton = v.findViewById(R.id.signup_button)
        loginTextView = v.findViewById(R.id.login_text_view)
        auth = FirebaseAuth.getInstance()

        initialiseSignup()
        initialiseLogIn()

        return v
    }

    private fun initialiseSignup() {
        signupButton.setOnClickListener {
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this.activity, getString(R.string.field_missing), Toast.LENGTH_LONG).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this.activity as Activity) { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this.activity, getString(R.string.signup_success), Toast.LENGTH_LONG).show()
                        startActivity(Intent(this.activity, MainActivity::class.java))
                    } else {
                        Toast.makeText(this.activity, getString(R.string.signup_failure), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initialiseLogIn() {
        loginTextView.setOnClickListener {
            viewPager.currentItem--
        }
    }
}