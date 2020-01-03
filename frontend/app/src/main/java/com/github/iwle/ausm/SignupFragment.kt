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
import com.github.iwle.ausm.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignupFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_signup, container, false)

        val viewPagerId = arguments!!.getInt("viewPagerId")
        viewPager = activity!!.findViewById(viewPagerId)

        firstNameEditText = v.findViewById(R.id.first_name_edit_text)
        lastNameEditText = v.findViewById(R.id.last_name_edit_text)
        emailEditText = v.findViewById(R.id.email_edit_text)
        passwordEditText = v.findViewById(R.id.password_edit_text)
        signupButton = v.findViewById(R.id.signup_button)
        loginTextView = v.findViewById(R.id.login_text_view)
        firebaseAuth = FirebaseAuth.getInstance()

        initialiseSignup()
        initialiseLogIn()

        return v
    }

    private fun initialiseSignup() {
        signupButton.setOnClickListener {
            val firstName: String = firstNameEditText.text.toString()
            val lastName: String = lastNameEditText.text.toString()
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this.activity, getString(R.string.field_missing), Toast.LENGTH_LONG).show()
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this.activity as Activity) { task ->
                    if(task.isSuccessful) {
                        // Store first and last name in users
                        val userUid: String = firebaseAuth.currentUser!!.uid
                        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                        val users: CollectionReference = firestore.collection("users")
                        users.document(userUid).set(
                            User(
                                firstName,
                                lastName
                            )
                        )

                        Toast.makeText(this.activity, getString(R.string.signup_success), Toast.LENGTH_LONG).show()
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