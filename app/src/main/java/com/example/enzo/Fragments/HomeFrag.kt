package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Models.AdModel
import com.example.enzo.Adapters.HomeRVAdapter
import com.example.enzo.MainActivity
import com.example.enzo.R
import com.example.enzo.SearchResult
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


class HomeFrag : Fragment() {

////////declaring/////////
    lateinit var auth: FirebaseAuth
    lateinit var fStore: FirebaseFirestore
    lateinit var recyclerView: RecyclerView
   lateinit var homeRVAdapter: HomeRVAdapter
    lateinit var adList: ArrayList<AdModel>
    lateinit var shimmerHomeAds:ShimmerFrameLayout
    lateinit var searchBarHome:Button
    lateinit var adIds:ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View= inflater.inflate(R.layout.fragment_home, container, false)

///////////initializing/////////
        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()


        adList= arrayListOf<AdModel>()
adIds= arrayListOf()

//////setting up recycler view and assigning adapter////////
        shimmerHomeAds=view.findViewById(R.id.shimmerHomeAds)
        recyclerView =view.findViewById(R.id.homeRView)
        searchBarHome=view.findViewById(R.id.searchBarHome)
        recyclerView.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)


///////////getting all ads displayed in recycler view///////////
        fStore.collection("ads")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(qs: QuerySnapshot?) {

                    for (qds: QueryDocumentSnapshot in qs!!) {
                        var adsId:String= qds.id.toString()
                        var displayAdTitle: String = qds.getString("adTitle").toString()
                        var displayAdPrice: String = qds.getString("adPrice").toString()
                        var displayAdImage:String= qds.getString("adImageUrl").toString()
                        var displayAdDetail:String= qds.getString("adDetail").toString()
                        var displayAdType:String= qds.getString("adType").toString()
                        var displayAdUserId:String= qds.getString("adUserId").toString()
                        var adSearchTitle:String= qds.getString("adSearchTitle").toString()
                        var adAllImages:String= qds.getString("adAllImages").toString()

                        adList.add(AdModel(displayAdTitle, displayAdDetail, displayAdPrice, displayAdImage, displayAdType, displayAdUserId, adSearchTitle , adAllImages, null, null ))
                        homeRVAdapter.notifyDataSetChanged()

                        adIds.add(adsId)
                    }

                    shimmerHomeAds.stopShimmer()
                    shimmerHomeAds.hideShimmer()
                    shimmerHomeAds.visibility=View.GONE
                }
            })
        homeRVAdapter= HomeRVAdapter(requireContext(), adList, adIds)
        recyclerView.adapter=homeRVAdapter

     ////setting on click of search bar button
        searchBarHome.setOnClickListener {
            val intent= Intent(context, SearchResult::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.activity_fade_out, R.anim.activity_fade_in)

        }

        ///////checking mvvm actvity
        val callback=object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
               requireActivity().finishAffinity()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)


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