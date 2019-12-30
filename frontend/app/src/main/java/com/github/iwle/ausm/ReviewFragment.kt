package com.github.iwle.ausm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReviewFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewListAdapter: ReviewListAdapter

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
        setUpList(v)
        return v
    }

    private fun setUpList(v: View) {
        // Fake data for testing UI
        val data = ArrayList<Establishment>()
        data.add(Establishment("https://images-na.ssl-images-amazon.com/images/I/81zfHiBLQBL._SX466_.jpg", "Pomeranian", "20 Clementi Ave 5", 4.8))
        data.add(Establishment("https://media.licdn.com/dms/image/C5103AQGoTP2bX1HwYA/profile-displayphoto-shrink_200_200/0?e=1583366400&v=beta&t=E-2pisrSdORszvYoWZfoVH9QRnIbpK8vGALFoz_WCT0", "Ong Wai Boon", "Bedok", 0.0))
        reviewListAdapter = ReviewListAdapter(data)
        recyclerView = v.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = reviewListAdapter
        }
    }
}