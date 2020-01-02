package com.github.iwle.ausm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.view_login, container, false)

        emailEditText = v.findViewById(R.id.email_edit_text)
        passwordEditText = v.findViewById(R.id.password_edit_text)
        loginButton = v.findViewById(R.id.login_button)

        auth = FirebaseAuth.getInstance()

        /*loginButton.setOnClickListener {
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this.activity, getString(R.string.login_field_missing), Toast.LENGTH_LONG).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this.activity, getString(R.string.login_success), Toast.LENGTH_LONG).show()
                        startActivity(Intent(this.activity, MainActivity::class.java))
                    } else {
                        Toast.makeText(this.activity, getString(R.string.login_failure), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }*/

        return v
    }
}