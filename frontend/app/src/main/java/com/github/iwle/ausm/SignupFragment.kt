package com.github.iwle.ausm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth

class SignupFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
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

        loginTextView = v.findViewById(R.id.login_text_view)
        auth = FirebaseAuth.getInstance()

        initialiseLogIn()

        return v
    }

    private fun initialiseLogIn() {
        loginTextView.setOnClickListener {
            viewPager.currentItem--
        }
    }
}