package com.example.enzo.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.ViewBidsAdapter
import com.example.enzo.Models.LoginModel
import com.example.enzo.OnClickRV.SwipeGestures
import com.example.enzo.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


class ViewBidsFrag : Fragment() {

    lateinit var viewBidsRV: RecyclerView
    lateinit var viewBidsAdapter: ViewBidsAdapter
    lateinit var fStore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var bidsList:ArrayList<LoginModel>
    lateinit var allBids:ArrayList<String>
    lateinit var bidIds:ArrayList<String>
    lateinit var adId:String
    lateinit var nothingImage: ImageView
    lateinit var nothingText: TextView
    lateinit var goBackBtn:ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view=inflater.inflate(R.layout.fragment_view_bids, container, false)

        adId= arguments?.getString("adId").toString()
        fStore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        viewBidsRV=view.findViewById(R.id.viewBidsRV)
        nothingImage=view.findViewById(R.id.searchNothingImage)
        nothingText=view.findViewById(R.id.searchNothingText)
        goBackBtn=view.findViewById(R.id.goBackAdBid)
        bidsList= arrayListOf()
        allBids= arrayListOf()
        bidIds= arrayListOf()
        ///viewBids recyclerView

        viewBidsRV.layoutManager= LinearLayoutManager(requireContext())
        viewBidsRV.setHasFixedSize(true)

        viewBidsAdapter= ViewBidsAdapter(requireContext(),bidsList,allBids,bidIds,adId, false )
        viewBidsRV.adapter=viewBidsAdapter

        val biddersIds=ArrayList<String>()
        bidsList.clear()
        allBids.clear()
        fStore.collection("ads").document(adId)
            .collection("bidding").get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(qs: QuerySnapshot?) {
                    if (qs!!.isEmpty) {
                        nothingImage.visibility=View.VISIBLE
                        nothingText.visibility = View.VISIBLE
                    } else {

                    for (qds: QueryDocumentSnapshot in qs!!) {

                        val bidId: String = qds.id.toString()
                        val idOfBidder: String = qds.get("bidderId").toString()
                        val bid: String = qds.getString("bid").toString()

                        biddersIds.add(idOfBidder)
                        allBids.add(bid)
                        bidIds.add(bidId)
                    }
                        if (biddersIds.size != 0) {
                            for (i in 0 until biddersIds.size) {

                                fStore.collection("users").document(biddersIds[i]).get()
                                    .addOnSuccessListener {
                                        val bidderName = it.get("profileName").toString()
                                        val bidderPic = it.get("profileUrl").toString()
                                        bidsList.add(
                                            LoginModel(
                                                bidderName,
                                                bidderPic,
                                                biddersIds[i]
                                            )
                                        )
                                        viewBidsAdapter.notifyDataSetChanged()
                                    }


                        }
                        }
                    }
                }
            })

        /////recycler view swipe gestures
        val swipeGesture = object : SwipeGestures(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {

                    ItemTouchHelper.LEFT -> {


                        MaterialAlertDialogBuilder(requireContext()).setTitle("Do you want to decline this Bid?")
                            .setNegativeButton("No") { dialog, it ->
                               viewBidsAdapter.notifyDataSetChanged()
                            }
                            .setPositiveButton("Yes, delete") { dialog, it ->

                                viewBidsAdapter.deleteItem(viewHolder.position)
                               viewBidsAdapter.notifyDataSetChanged()
                                val sb = Snackbar.make(
                                    viewBidsRV,
                                    "Bid declined & removed.",
                                    Snackbar.LENGTH_SHORT
                                )
                                sb.setAction("Got It") {
                                    sb.dismiss()
                                }.show()
                            }
                            .show()
                    }
                }


            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(viewBidsRV)

////navigating back to addFragon onBackPress
        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                    findNavController().navigate(R.id.action_viewBidsFrag_to_myAdsFrag)


            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)


        goBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_viewBidsFrag_to_myAdsFrag)
        }

        return view
    }

}