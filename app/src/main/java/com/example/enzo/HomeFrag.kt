package com.example.enzo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.R
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
        recyclerView =view.findViewById(R.id.homeRView)
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
                        adList.add(AdModel(displayAdTitle, displayAdDetail, displayAdPrice, displayAdImage, displayAdType, displayAdUserId  ))
                        homeRVAdapter.notifyDataSetChanged()
                    }
                }
            })

        return view
    }//oncreate

}//main