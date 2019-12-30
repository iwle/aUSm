package com.github.iwle.ausm

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ReviewFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewListAdapter: ReviewListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        fun newInstance() : ReviewFragment {
            return ReviewFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_review, container, false)
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener(this)
        fetchData()
        recyclerView = v.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = reviewListAdapter
        }
        return v
    }

    private fun fetchData() {
        // Fake data for testing UI
        val data = ArrayList<Establishment>()
        data.add(Establishment("https://images-na.ssl-images-amazon.com/images/I/81zfHiBLQBL._SX466_.jpg", "Pomeranian", "20 Clementi Ave 5", 4.8))
        data.add(Establishment("https://media.licdn.com/dms/image/C5103AQGoTP2bX1HwYA/profile-displayphoto-shrink_200_200/0?e=1583366400&v=beta&t=E-2pisrSdORszvYoWZfoVH9QRnIbpK8vGALFoz_WCT0", "Ong Wai Boon", "Bedok", 0.0))
        data.add(Establishment("https://www.comp.nus.edu.sg/stfphotos/sooyj_2.jpg", "Soo Yuen Jien", "NUS", 5.0))
        data.add(Establishment("https://media-cdn.tripadvisor.com/media/photo-s/02/39/56/bd/side-entrance.jpg", "Starbucks","Plaza Singapura", 4.1))
        data.add(Establishment("https://images-na.ssl-images-amazon.com/images/I/81zfHiBLQBL._SX466_.jpg", "Another Pomeranian", "lorem ipsum", 4.9))
        reviewListAdapter = ReviewListAdapter(data)

        // Stop the refresh animation
        val runnable = Runnable {
            if(swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
        Handler().postDelayed(runnable, 1000)
    }

    override fun onRefresh() {
        fetchData()
    }
}