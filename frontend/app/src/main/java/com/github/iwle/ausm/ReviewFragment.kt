package com.github.iwle.ausm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.iwle.ausm.adapter.EstablishmentAdapter
import com.github.iwle.ausm.model.Establishment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ReviewFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var establishmentAdapter: EstablishmentAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var establishmentList: ArrayList<Establishment>
    private lateinit var establishments: CollectionReference

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
        firestore = FirebaseFirestore.getInstance()
        establishments = firestore.collection("establishments")

        val v = inflater.inflate(R.layout.fragment_review, container, false)
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener(this)
        // Set refresh animation colour cycle
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorSecondary)
        // Initialise reviewListAdapter
        establishmentList = ArrayList()
        establishmentAdapter = EstablishmentAdapter(establishmentList) { establishment: Establishment ->
            onEstablishmentClicked(establishment)
        }
        fetchData()
        recyclerView = v.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = establishmentAdapter
        }
        return v
    }

    private fun onEstablishmentClicked(establishment: Establishment) {
        val intent: Intent = Intent(this.activity, EstablishmentDetailsActivity::class.java)
        intent.putExtra("establishment", establishment)
        startActivity(intent)
    }

    private fun fetchData() {
        establishments.get().addOnSuccessListener { querySnapshot ->
            establishmentList.clear()
            for(documentSnapshot in querySnapshot) {
                val establishment = documentSnapshot.toObject(Establishment::class.java)
                establishmentList.add(establishment)
            }
            // Update reviewListAdapter
            establishmentAdapter.notifyDataSetChanged()

            // Stop the refresh animation
            val runnable = Runnable {
                if(swipeRefreshLayout.isRefreshing) {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
            Handler().postDelayed(runnable, 1000)
        }
    }

    override fun onRefresh() {
        fetchData()
    }
}