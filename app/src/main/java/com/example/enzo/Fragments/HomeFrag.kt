package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Models.AdModel
import com.example.enzo.Adapters.HomeRVAdapter
import com.example.enzo.R
import com.example.enzo.SearchResult
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.miguelcatalan.materialsearchview.MaterialSearchView


class HomeFrag : Fragment() {

////////declaring/////////
    lateinit var auth: FirebaseAuth
    lateinit var fStore: FirebaseFirestore
    lateinit var recyclerView: RecyclerView
   lateinit var homeRVAdapter: HomeRVAdapter
    lateinit var adList: ArrayList<AdModel>
    lateinit var shimmerHomeAds:ShimmerFrameLayout
    lateinit var searchBarHome:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View= inflater.inflate(R.layout.fragment_home, container, false)

///////////initializing/////////
        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()


        adList= arrayListOf<AdModel>()

//////setting up recycler view and assigning adapter////////
        shimmerHomeAds=view.findViewById(R.id.shimmerHomeAds)
        recyclerView =view.findViewById(R.id.homeRView)
        searchBarHome=view.findViewById(R.id.searchBarHome)
        recyclerView.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        homeRVAdapter= HomeRVAdapter(requireContext(), adList)
        recyclerView.adapter=homeRVAdapter

///////////getting all ads displayed in recycler view///////////
        fStore.collection("ads")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(qs: QuerySnapshot?) {

                    for (qds: QueryDocumentSnapshot in qs!!) {
                        var displayAdTitle: String = qds.getString("adTitle").toString()
                        var displayAdPrice: String = qds.getString("adPrice").toString()
                        var displayAdImage:String= qds.getString("adImageUrl").toString()
                        var displayAdDetail:String= qds.getString("adDetail").toString()
                        var displayAdType:String= qds.getString("adType").toString()
                        var displayAdUserId:String= qds.getString("adUserId").toString()
                        var adSearchTitle:String= qds.getString("adSearchTitle").toString()

                        adList.add(AdModel(displayAdTitle, displayAdDetail, displayAdPrice, displayAdImage, displayAdType, displayAdUserId, adSearchTitle  ))
                        homeRVAdapter.notifyDataSetChanged()

                    }
                    shimmerHomeAds.stopShimmer()
                    shimmerHomeAds.hideShimmer()
                    shimmerHomeAds.visibility=View.GONE
                }
            })

     ////setting on click of search bar button
        searchBarHome.setOnClickListener {
            val intent= Intent(context, SearchResult::class.java)
            startActivity(intent)
        }

        ///////checking mvvm actvity


        return view
    }//oncreate

    override fun onPause() {
        shimmerHomeAds.hideShimmer()
        shimmerHomeAds.stopShimmer()
        shimmerHomeAds.visibility=View.GONE
        super.onPause()
    }

    override fun onResume() {
        shimmerHomeAds.startShimmer()
        super.onResume()
    }

}//main