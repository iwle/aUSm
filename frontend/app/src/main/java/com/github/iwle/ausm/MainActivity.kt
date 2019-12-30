package com.github.iwle.ausm

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout

class MainActivity : FragmentActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialiseTabPagerAdapter()
    }

    private fun initialiseTabPagerAdapter() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.tab_view_pager)
        toolbar = findViewById(R.id.toolbar)
        val tabFragmentList = listOf(MapFragment(), ReviewFragment())
        val tabNameList = listOf(getString(R.string.tab_gps), getString(R.string.tab_reviews))
        val tabPagerAdapter = TabPagerAdapter(this, tabFragmentList)
        viewPager.adapter = tabPagerAdapter
        tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                toolbar.title = tabNameList[tab.position]
                // Show app bar when tab is selected
                findViewById<AppBarLayout>(R.id.app_bar_layout)?.setExpanded(true, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Show app bar when tab is reselected
                findViewById<AppBarLayout>(R.id.app_bar_layout)?.setExpanded(true, true)
            }
        })
        // Disable swiping of view pager
        viewPager.isUserInputEnabled = false
        // Set initial toolbar title
        toolbar.title = tabNameList[0]
    }
}