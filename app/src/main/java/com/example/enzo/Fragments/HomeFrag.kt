package com.example.enzo.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.enzo.Adapters.HomeCategoryAdapter
import com.example.enzo.Models.AdModel
import com.example.enzo.Adapters.HomeRVAdapter
import com.example.enzo.Adapters.HomeSocialMediaAdapter
import com.example.enzo.OnClickRV.HomeRVOnClick
import com.example.enzo.R
import com.example.enzo.SearchResult
import com.example.enzo.TryActivity
import com.example.enzo.ViewAdActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.suspendCoroutine


class HomeFrag : Fragment(), HomeRVOnClick {

////////declaring/////////
    lateinit var auth: FirebaseAuth
    lateinit var fStore: FirebaseFirestore
    lateinit var allAdsRV: RecyclerView
   lateinit var homeRVAdapter: HomeRVAdapter
   lateinit var socialMediaRV:RecyclerView
   lateinit var socialMediaAdapter:HomeSocialMediaAdapter
   lateinit var profilePicHomeFrag: ImageView
    lateinit var adList: ArrayList<AdModel>
    lateinit var socialMediaAdsList:ArrayList<AdModel>
    lateinit var socialMediaAdIds:ArrayList<String>
    lateinit var shimmerHomeAds:ShimmerFrameLayout
    lateinit var socialMediaShimmer:ShimmerFrameLayout
    lateinit var searchBarHome:CardView
    lateinit var adIds:ArrayList<String>
    lateinit var categoryGV:GridView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View= inflater.inflate(R.layout.fragment_home, container, false)


try {


    val navBar: BottomNavigationView = requireActivity().findViewById(R.id.navBar)
    navBar.visibility = View.VISIBLE
}catch (e:Exception){
    Log.d("","")
}
    /*    val goTry:Button=view.findViewById(R.id.goTry)
        goTry.setOnClickListener {
            Intent(requireContext(), TryActivity::class.java).apply {
                startActivity(this)
        }
        }*/
///////////initializing/////////
        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
         profilePicHomeFrag=view.findViewById(R.id.profilePicHomeFrag)


        adList= arrayListOf<AdModel>()
        adIds= arrayListOf()

        socialMediaAdsList= arrayListOf()
        socialMediaAdIds= arrayListOf()
//////setting up home category list to click
        val homeCategoryRV: RecyclerView=view.findViewById(R.id.homeCategoryRV)
        val categoryNames: Array<String>
        val categoryIcons: Array<Int>
        categoryNames= arrayOf("Freelance", "Gaming", "Instagram", "Facebook", "Youtube", "Influencers", "Other")
        val categories: Array<String>
        categories= arrayOf("freelancing", "gaming", "instagram", "facebook", "youtube", "influencer", "other")

        categoryIcons= arrayOf(R.drawable.categoryfreelance,
            R.drawable.categorygaming,
            R.drawable.categoryinstagram,
            R.drawable.categoryfacebook,
            R.drawable.categoryyoutube,
            R.drawable.categoryinfluencer,
            R.drawable.categoryother)

        homeCategoryRV.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        homeCategoryRV.setHasFixedSize(true)
        val homeCategoryAdapter= HomeCategoryAdapter(requireContext(), categoryNames, categoryIcons, categories)
        homeCategoryRV.adapter=homeCategoryAdapter
        homeCategoryAdapter.notifyDataSetChanged()

//////setting up recycler view and assigning adapter////////
        shimmerHomeAds=view.findViewById(R.id.shimmerHomeAds)
        socialMediaShimmer=view.findViewById(R.id.shimmersocialMediaAds)


        allAdsRV =view.findViewById(R.id.homeRView)
        socialMediaRV=view.findViewById(R.id.socialMediaRV)
        searchBarHome=view.findViewById(R.id.searchBarHome)
        allAdsRV.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        allAdsRV.setHasFixedSize(true)
        socialMediaRV.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        socialMediaRV.setHasFixedSize(true)


        homeRVAdapter= HomeRVAdapter(requireContext(), adList, adIds, this)
        allAdsRV.adapter=homeRVAdapter

        socialMediaAdapter=HomeSocialMediaAdapter(requireContext(), socialMediaAdsList, socialMediaAdIds)
        socialMediaRV.adapter=socialMediaAdapter

/////setting user profile pic to dashboard pic
        displayingUserProfileDashboard()
////displaying all ads in first RV
        displayAllAdsScrollRV()
///displaying socialMediaAds
        displaySocialMediaAds()

  profilePicHomeFrag.setOnClickListener {
    findNavController().navigate(R.id.action_homeFrag_to_profileFrag2)
    }

     ////setting on click of search bar button
        searchBarHome.setOnClickListener {

            /////shared element transition

            sharedElementTransitionSearchBar()


        }

     ///onBackPressed
        val callback=object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
               requireActivity().finishAffinity()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)


        return view
    }//oncreate

    private fun displaySocialMediaAds() {
        lifecycleScope.async (Dispatchers.IO) {


            val job=async {
                try {


                    fStore.collection("ads")
                        .get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                            override fun onSuccess(querySnapshot: QuerySnapshot?) {
                                for (qds: QueryDocumentSnapshot in querySnapshot!!) {

                                    val adId: String = qds.id.toString()
                                    val displayAdTitle: String = qds.getString("adTitle").toString()
                                    var displayAdPrice: String = qds.getString("adPrice").toString()
                                    var displayAdImage: String = qds.getString("adImageUrl").toString()
                                    var displayAdDetail: String = qds.getString("adDetail").toString()
                                    var displayAdType: String = qds.getString("adType").toString()
                                    var displayAdUserId: String = qds.getString("adUserId").toString()
                                    var displayAdSearchTitle: String = qds.getString("adSearchTitle").toString()
                                    var allImagesUrl: String = qds.getString("adAllImages").toString()
                                    var adPhoneNo: String = qds.getString("adPhoneNo").toString()
                                    var adLocation: String = qds.getString("adLocation").toString()

                                   if (displayAdType.contains("facebook")) {

                                       socialMediaAdsList.add(
                                           AdModel(
                                               adTitle = displayAdTitle,
                                               adDetail = displayAdDetail,
                                               adPrice = displayAdPrice,
                                               adImageUrl = displayAdImage,
                                               adType = displayAdType,
                                               adUserId = displayAdUserId,
                                               adSearchTitle = displayAdSearchTitle,
                                               adAllImages = allImagesUrl,
                                               adPhoneNo,
                                               adLocation
                                           )
                                       )

                                       socialMediaAdapter.notifyDataSetChanged()
                                       socialMediaAdIds.add(adId)
                                   }else if (displayAdType.contains("instagram")){
                                       socialMediaAdsList.add(
                                           AdModel(
                                               adTitle = displayAdTitle,
                                               adDetail = displayAdDetail,
                                               adPrice = displayAdPrice,
                                               adImageUrl = displayAdImage,
                                               adType = displayAdType,
                                               adUserId = displayAdUserId,
                                               adSearchTitle = displayAdSearchTitle,
                                               adAllImages = allImagesUrl,
                                               adPhoneNo,
                                               adLocation
                                           )
                                       )

                                       socialMediaAdapter.notifyDataSetChanged()
                                       socialMediaAdIds.add(adId)
                                   } else if (displayAdType.contains("youtube")){
                                       socialMediaAdsList.add(
                                           AdModel(
                                               adTitle = displayAdTitle,
                                               adDetail = displayAdDetail,
                                               adPrice = displayAdPrice,
                                               adImageUrl = displayAdImage,
                                               adType = displayAdType,
                                               adUserId = displayAdUserId,
                                               adSearchTitle = displayAdSearchTitle,
                                               adAllImages = allImagesUrl,
                                               adPhoneNo,
                                               adLocation
                                           )
                                       )

                                       socialMediaAdapter.notifyDataSetChanged()
                                       socialMediaAdIds.add(adId)
                                   }else{

                                   }


                                }

                                socialMediaShimmer.visibility = View.GONE
                            }
                        })
                } catch (e: Exception) {
                    Log.d("err", "")
                }
            }
        }
    }

    private fun sharedElementTransitionSearchBar() {
       //to send Image as shared element to next activity

       val p1: Pair<View, String>
        p1= Pair(searchBarHome, "searchBarHomeTrans")
        val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p1)
        val intent= Intent(requireContext(), SearchResult::class.java)
        startActivity(intent, extras.toBundle())

    }

    private fun displayAllAdsScrollRV() {

        lifecycleScope.async(Dispatchers.IO) {
            try {

          val job1=async {
                fStore.collection("ads")
                    .get()
                    .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                        override fun onSuccess(qs: QuerySnapshot?) {

                            for (qds: QueryDocumentSnapshot in qs!!) {
                                var adsId: String = qds.id.toString()
                                var displayAdTitle: String = qds.getString("adTitle").toString()
                                var displayAdPrice: String = qds.getString("adPrice").toString()
                                var displayAdImage: String = qds.getString("adImageUrl").toString()
                                var displayAdDetail: String = qds.getString("adDetail").toString()
                                var displayAdType: String = qds.getString("adType").toString()
                                var displayAdUserId: String = qds.getString("adUserId").toString()
                                var adSearchTitle: String = qds.getString("adSearchTitle").toString()
                                var adAllImages: String = qds.getString("adAllImages").toString()
                                var adPhoneNo:String=qds.getString("adPhoneNo").toString()
                                var adLocation:String=qds.getString("adLocation").toString()

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
                                        adPhoneNo,
                                        adLocation
                                    )
                                )
                                homeRVAdapter.notifyDataSetChanged()

                                adIds.add(adsId)
                            }

                            shimmerHomeAds.stopShimmer()
                            shimmerHomeAds.hideShimmer()
                            shimmerHomeAds.visibility = View.GONE
                        }
                    })
          }
            }catch (e:Exception){
                Log.e("","")
            }
        }
    }


    private fun displayingUserProfileDashboard() {
        lifecycleScope.async(Dispatchers.IO) {
        try {



          val job=  async {
              fStore.collection("users").document(auth.currentUser!!.uid.toString()).get()
                  .addOnSuccessListener {

                      val picUrl: String = it.getString("profileUrl").toString()
                          Picasso.get().load(picUrl).placeholder(R.drawable.blankuser)
                              .into(profilePicHomeFrag)

                  }
          }

            }
        catch (e:Exception){
            Log.e("","")
        }
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
        intent.putExtra("adLocation", adList[pos].adLocation)
        intent.putExtra("adPhoneNo",  adList[pos].adPhoneNo)
        intent.putExtra("adId", adIds[pos])


///to send image as shared element
      /*  val p2: Pair<View, String>
        p2= Pair(adImage, "viewAdImgTrans")
*/
        val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()/*, p2*/)
        startActivity(intent/*, extras.toBundle()*/)

        super.onAdItemClick(pos, adImage)
    }


    override fun onPause() {

        shimmerHomeAds.visibility=View.GONE
        super.onPause()
    }

    override fun onResume() {
        shimmerHomeAds.startShimmer()
        super.onResume()
    }
}//main