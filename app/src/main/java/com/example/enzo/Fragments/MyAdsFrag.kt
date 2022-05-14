package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import android.transition.ChangeBounds
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.MyAdsAdapter
import com.example.enzo.OnClickRV.SwipeGestures
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.MyAdsOnClick
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.example.enzo.ViewModels.MyAdsVM
import com.example.enzo.ViewModels.SavedAdsVM
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class MyAdsFrag : Fragment(), MyAdsOnClick {

   
   
    lateinit var fStore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var myAdList:ArrayList<AdModel>
    lateinit var adIds:ArrayList<String>
    lateinit var imgLinks:ArrayList<String>
    lateinit var adAllImgLinkList:ArrayList<String>
    lateinit var shimmerMyAds:ShimmerFrameLayout
    lateinit var myAdsRVAdapter:MyAdsAdapter
    lateinit var myAdsRV: RecyclerView
    lateinit var searchNothingImage: ImageView
    lateinit var searchNothingText: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_my_ads, container, false)
        sharedElementEnterTransition=ChangeBounds()
        fStore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        shimmerMyAds=view.findViewById(R.id.shimmerMyAds)
        searchNothingImage=view.findViewById(R.id.searchNothingImage)
        searchNothingText=view.findViewById(R.id.searchNothingText)
        searchNothingImage.visibility=View.GONE
        searchNothingText.visibility=View.GONE
        myAdList= arrayListOf()
        adIds= arrayListOf()
        imgLinks= arrayListOf()
        adAllImgLinkList= arrayListOf()

        myAdsRV= view.findViewById(R.id.myAdsRV)


        myAdsRV.layoutManager= LinearLayoutManager(requireContext())
        myAdsRV.setHasFixedSize(true)

        myAdsRVAdapter= MyAdsAdapter(requireContext(), myAdList, adIds, adAllImgLinkList , this)
        myAdsRV.adapter=myAdsRVAdapter

        myAdList.clear()
        adIds.clear()

///getting My Ads through ViewModel
        val viewModel = ViewModelProvider(this)[MyAdsVM::class.java]
        viewModel.getMyAds().observe(viewLifecycleOwner, Observer { userList ->

            myAdList.clear()
            myAdList.addAll(userList)
            if (myAdList.isEmpty()) {


                shimmerMyAds.visibility = View.VISIBLE
                myAdsRVAdapter.notifyDataSetChanged()
            } else {

                shimmerMyAds.visibility = View.GONE
                myAdsRVAdapter.notifyDataSetChanged()
            }
        })


////on Back Press
        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_myAdsFrag_to_profileFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

  /////recycler view swipe gestures
        val swipeGesture= object : SwipeGestures(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {

                    ItemTouchHelper.LEFT -> {


                        MaterialAlertDialogBuilder(requireContext()).setTitle("Want to delete you Ad?")
                            .setMessage("All the data and images will be deleted permanently!")
                            .setNegativeButton("No"){dialog, it->
                               myAdsRVAdapter.notifyDataSetChanged()
                            }
                            .setPositiveButton("Yes, delete"){dialog, it->

                               myAdsRVAdapter.deleteItem(viewHolder.position)

                                val sb=Snackbar.make(myAdsRV, "Ad deleted successfully", Snackbar.LENGTH_SHORT)
                                sb.setAction("Got It"){
                                    sb.dismiss()
                                }.show()
                            }
                            .show()
                    }
                }


            }
        }

        val touchHelper= ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(myAdsRV)

        return view
    }///////////////onCreate


    override fun onAdItemClick(pos: Int, adImage: ImageView) {
        val intent= Intent(requireContext(), ViewAdActivity::class.java)
        intent.putExtra("adViewImage", myAdList[pos].adImageUrl)
        intent.putExtra("adViewTitle", myAdList[pos].adTitle)
        intent.putExtra("adViewPrice", myAdList[pos].adPrice)
        intent.putExtra("adViewDetail", myAdList[pos].adDetail)
        intent.putExtra("idOfUploader", myAdList[pos].adUserId)
        intent.putExtra("adAllImages", myAdList[pos].adAllImages)
        intent.putExtra("adId", myAdList[pos].adId)


        val p2: Pair<View, String>
        p2= Pair(adImage, "viewAdImgTrans")

        val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p2)
        startActivity(intent, extras.toBundle())

        super.onAdItemClick(pos, adImage)
    }
}//main