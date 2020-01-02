package com.github.iwle.ausm

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.github.iwle.ausm.adapter.TabPagerAdapter

class LoginActivity : FragmentActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)

        viewPager = findViewById(R.id.authenticate_view_pager)
        // Disable swiping
        viewPager.isUserInputEnabled = false
        initialiseViewPager()
    }

    private fun initialiseViewPager() {
        val bundle = Bundle()
        bundle.putInt("viewPagerId", R.id.authenticate_view_pager)

        val loginFragment = LoginFragment()
        loginFragment.arguments = bundle
        val signupFragment = SignupFragment()
        signupFragment.arguments = bundle
        val cardFragmentList = listOf(loginFragment, signupFragment)

        viewPager.adapter =
            TabPagerAdapter(this, cardFragmentList)
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