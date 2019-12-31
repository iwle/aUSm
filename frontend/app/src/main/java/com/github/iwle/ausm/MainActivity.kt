package com.github.iwle.ausm

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs

class MainActivity : FragmentActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var floatingActionButton: ExtendedFloatingActionButton
    private var isFabEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.tab_view_pager)
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.app_bar_layout)
        floatingActionButton = findViewById(R.id.extended_floating_action_button)

        initialiseTabPagerAdapter()
        initialiseFloatingActionButton()
    }

    private fun initialiseTabPagerAdapter() {
        val tabFragmentList = listOf(MapFragment(), ReviewFragment())
        val tabNameList = listOf(getString(R.string.tab_gps), getString(R.string.tab_reviews))
        val tabPagerAdapter = TabPagerAdapter(this, tabFragmentList)
        viewPager.adapter = tabPagerAdapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                // Show app bar when tab is selected
                appBarLayout.setExpanded(true, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Show app bar when tab is reselected
                appBarLayout.setExpanded(true, true)
            }
        })
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                toolbar.title = tabNameList[position]
                // Show extended FAB only on Reviews tab
                when(position) {
                    0 -> updateFabStatus(false)
                    1 -> updateFabStatus(true)
                }
            }
        })
        // Disable swiping of view pager
        viewPager.isUserInputEnabled = false
        // Set initial toolbar title
        toolbar.title = tabNameList[0]
    }

    private fun initialiseFloatingActionButton() {
        // Hide extended FAB when toolbar is collapsed
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if(abs(verticalOffset) - appBarLayout!!.totalScrollRange == 0) {
                // Collapsed
                hideFab()
            } else {
                // Expanded
                showFab()
            }
        })
    }

    private fun updateFabStatus(status: Boolean) {
        isFabEnabled = status
        if(isFabEnabled) {
            showFab()
        } else {
            hideFab()
        }
    }

    private fun showFab() {
        if(isFabEnabled) {
            floatingActionButton.show()
        }
    }

    private fun hideFab() {
        floatingActionButton.hide()
    }
}