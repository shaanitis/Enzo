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
    lateinit var adList:ArrayList<AdModel>
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
        adList= arrayListOf()
        adIds= arrayListOf()
        imgLinks= arrayListOf()
        adAllImgLinkList= arrayListOf()

        myAdsRV= view.findViewById(R.id.myAdsRV)


        myAdsRV.layoutManager= LinearLayoutManager(requireContext())
        myAdsRV.setHasFixedSize(true)

        myAdsRVAdapter= MyAdsAdapter(requireContext(), adList, adIds, adAllImgLinkList , this)
        myAdsRV.adapter=myAdsRVAdapter

        adList.clear()
        adIds.clear()

///getting my Ads
     getMyAds()

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

    private fun getMyAds() {
        lifecycleScope.async(Dispatchers.IO) {
            try {



                val job = async {
                    fStore.collection("ads")
                        .whereEqualTo("adUserId", auth.currentUser?.uid.toString())
                        .get()
                        .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                            override fun onSuccess(qs: QuerySnapshot?) {
                                if (qs!!.isEmpty){
                                    searchNothingImage.visibility=View.VISIBLE
                                    searchNothingText.visibility=View.VISIBLE
                                    shimmerMyAds.visibility = View.GONE
                                } else {

                                    for (qds: QueryDocumentSnapshot in qs!!) {


                                            var adsId: String = qds.id.toString()
                                            var displayAdTitle: String =
                                                qds.getString("adTitle").toString()
                                            var displayAdPrice: String =
                                                qds.getString("adPrice").toString()
                                            var displayAdImage: String =
                                                qds.getString("adImageUrl").toString()
                                            var displayAdDetail: String =
                                                qds.getString("adDetail").toString()
                                            var displayAdType: String =
                                                qds.getString("adType").toString()
                                            var displayAdUserId: String =
                                                qds.getString("adUserId").toString()
                                            var adSearchTitle: String =
                                                qds.getString("adSearchTitle").toString()
                                            var adAllImages: String =
                                                qds.getString("adAllImages").toString()
                                            var adLocation: String =
                                                qds.getString("adLocation").toString()
                                            var adPhoneNo: String =
                                                qds.getString("adPhoneNo").toString()


                                            myAdsRV.startLayoutAnimation()
                                            adList.add(
                                                AdModel(
                                                    displayAdTitle,
                                                    displayAdDetail,
                                                    displayAdPrice,
                                                    displayAdImage,
                                                    displayAdType,
                                                    displayAdUserId,
                                                    adSearchTitle,
                                                    adAllImages,
                                                    adLocation,
                                                    adPhoneNo
                                                )
                                            )
                                            adIds.add(adsId)
                                            adAllImgLinkList.add(adAllImages)
                                            myAdsRVAdapter.notifyDataSetChanged()
                                            searchNothingImage.visibility = View.GONE
                                            searchNothingText.visibility = View.GONE
                                            shimmerMyAds.visibility = View.GONE

                                        }



                                myAdsRVAdapter.notifyDataSetChanged()
                                }
                            }
                        })
                }.await()
            }catch (e:Exception){
                Log.d("hi","")
            }
            myAdsRVAdapter.notifyDataSetChanged()
        }
    }

    override fun onAdItemClick(pos: Int, adImage: ImageView) {
        val intent= Intent(requireContext(), ViewAdActivity::class.java)
        intent.putExtra("adViewImage", adList[pos].adImageUrl)
        intent.putExtra("adViewTitle", adList[pos].adTitle)
        intent.putExtra("adViewPrice", adList[pos].adPrice)
        intent.putExtra("adViewDetail", adList[pos].adDetail)
        intent.putExtra("idOfUploader", adList[pos].adUserId)
        intent.putExtra("adAllImages", adList[pos].adAllImages)



        val p2: Pair<View, String>
        p2= Pair(adImage, "viewAdImgTrans")

        val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p2)
        startActivity(intent, extras.toBundle())

        super.onAdItemClick(pos, adImage)
    }
}//main