package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.SavedAdsAdapter
import com.example.enzo.OnClickRV.SwipeGestures
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.SavedAdsOnClick
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


class NotifyFrag : Fragment(), SavedAdsOnClick {
    lateinit var fStore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var adList:ArrayList<AdModel>
    lateinit var savedAdIds:ArrayList<String>
    lateinit var adIds:ArrayList<String>
    lateinit var shimmerSavedAds:ShimmerFrameLayout
    lateinit var searchNothingImage: ImageView
    lateinit var searchNothingText: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       val view:View= inflater.inflate(R.layout.fragment_notify, container, false)


        fStore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        shimmerSavedAds=view.findViewById(R.id.shimmerSavedAds)
        searchNothingImage=view.findViewById(R.id.searchNothingImage)
        searchNothingText=view.findViewById(R.id.searchNothingText)
        searchNothingImage.visibility=View.GONE
        searchNothingText.visibility=View.GONE
        adList= arrayListOf()
        savedAdIds= arrayListOf()
        adIds= arrayListOf()


        val savedAdsRV: RecyclerView = view.findViewById(R.id.savedAdsRV)


       savedAdsRV.layoutManager= LinearLayoutManager(requireContext())
        savedAdsRV.setHasFixedSize(true)

        val savedAdsRVAdapter= SavedAdsAdapter(requireContext(), adList, adIds , this)
        savedAdsRV.adapter=savedAdsRVAdapter

        adList.clear()
        adIds.clear()


////checking if saved ad of current user exists in saved ads list
    fStore.collection("savedAds").whereEqualTo("userId", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                searchNothingImage.visibility = View.VISIBLE
                searchNothingText.visibility = View.VISIBLE
                shimmerSavedAds.hideShimmer()
                shimmerSavedAds.stopShimmer()
                shimmerSavedAds.visibility=View.GONE
            }

//////getting saved ads
        fStore.collection("savedAds")
            .whereEqualTo("userId", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(querySnapshot: QuerySnapshot?) {
                    for (it: QueryDocumentSnapshot in querySnapshot!!){
                        val adId:String= it.id.toString()
                        val userId:String= it.getString("userId").toString()


                            savedAdIds.add(adId)

                    }
                    for (i in  0 until savedAdIds.size){
                        fStore.collection("ads").document(savedAdIds[i]).get().addOnSuccessListener {
                            val adId:String= it.id.toString()
                            val displayAdTitle: String = it.getString("adTitle").toString()
                            var displayAdPrice: String = it.getString("adPrice").toString()
                            var displayAdImage:String= it.getString("adImageUrl").toString()
                            var displayAdDetail:String= it.getString("adDetail").toString()
                            var displayAdType:String= it.getString("adType").toString()
                            var displayAdUserId:String= it.getString("adUserId").toString()
                            var displayAdSearchTitle:String= it.getString("adSearchTitle").toString()
                            var allImagesUrl:String= it.getString("adAllImages").toString()
                            var adPhoneNo:String= it.getString("adPhoneNo").toString()
                            var adLocation:String= it.getString("adLocation").toString()

                            searchNothingImage.visibility=View.GONE
                            searchNothingText.visibility=View.GONE
                            savedAdsRV.startLayoutAnimation()
                            adList.add(AdModel(adTitle = displayAdTitle ,
                                adDetail = displayAdDetail,
                                adPrice = displayAdPrice,
                                adImageUrl = displayAdImage,
                                adType = displayAdType,
                                adUserId = displayAdUserId,
                                adSearchTitle = displayAdSearchTitle,
                                adAllImages = allImagesUrl,
                                adPhoneNo =  adPhoneNo,
                                adLocation =  adLocation))

                            adIds.add(adId)
                            savedAdsRVAdapter.notifyDataSetChanged()
                            shimmerSavedAds.stopShimmer()
                            shimmerSavedAds.hideShimmer()
                            shimmerSavedAds.visibility=View.GONE


                        }
                        savedAdsRVAdapter.notifyDataSetChanged()

                    }


                }

            })


        val swipeGesture= object : SwipeGestures(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {

                    ItemTouchHelper.LEFT -> {

                        MaterialAlertDialogBuilder(requireContext()).setTitle("Want to remove Ad from your list?")
                            .setNegativeButton("No"){dialog, it->
                                     savedAdsRVAdapter.notifyDataSetChanged()
                            }
                            .setPositiveButton("Yes, delete"){dialog, it->

                                savedAdsRVAdapter.deleteItem(viewHolder.position)
                            }
                            .show()

                    }
                }


            }
        }

        val touchHelper= ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(savedAdsRV)





        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_notifyFrag_to_homeFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)



        return view
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

}


