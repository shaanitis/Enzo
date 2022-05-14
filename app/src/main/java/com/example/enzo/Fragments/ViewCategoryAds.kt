package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.SearchResultRVAdapter
import com.example.enzo.Adapters.ViewCategoryAdsAdapter
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.ViewCategoryRVOnClick
import com.example.enzo.R
import com.example.enzo.SearchResult
import com.example.enzo.ViewAdActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewCategoryAds : Fragment(), ViewCategoryRVOnClick {
       lateinit var fStore: FirebaseFirestore
       lateinit var auth:FirebaseAuth
    lateinit var adList: ArrayList<AdModel>
    lateinit var adIds:ArrayList<String>
    lateinit var categoryAdsRV:RecyclerView
    lateinit var categoryTitleText:TextView
    lateinit var categoryTitleIcon:ImageView
    lateinit var searchBar:CardView
    lateinit var viewCategoryAdIocn:ImageView
   lateinit var categoryAdsRVAdapter:ViewCategoryAdsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_view_category_ads, container, false)
        categoryAdsRV= view.findViewById(R.id.categoryAdsRV)
        categoryTitleText=view.findViewById(R.id.categoryTitleBarText)
        viewCategoryAdIocn=view.findViewById(R.id.viewCategortAdIcon)
        searchBar=view.findViewById(R.id.searchBar)
        fStore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()



        try {


            val category = requireArguments().getString("homeCategory")
            val categoryName = requireArguments().getString("homeCategoryName")

            categoryTitleText.text = categoryName+ " Accounts"


                adList = arrayListOf()
                adIds = arrayListOf()
                categoryAdsRV.layoutManager = LinearLayoutManager(requireContext())
                categoryAdsRV.setHasFixedSize(true)

                categoryAdsRVAdapter = ViewCategoryAdsAdapter(requireContext(), adList, adIds, this)
                categoryAdsRV.adapter = categoryAdsRVAdapter

                adList.clear()


                  gettingAds(category)

            callCategoryIcons(category)

                searchBar.setOnClickListener {
                    /////shared element transition
                try {


                    val p1: Pair<View, String>
                    p1 = Pair(searchBar, "searchBarHomeTrans")
                    val extras =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p1)

                    val intent = Intent(requireContext(), SearchResult::class.java)
                    startActivity(intent, extras.toBundle())
                }catch (e:Exception){
                    Log.d("err","")
                }
              /*      requireActivity().overridePendingTransition(0, 0);*/

                }




        }catch (e:Exception){
            Log.d("", "")
        }

////navigating back to homeFrag on onBackPress
        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_viewCategoryAds_to_homeFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)




        return view
    }

    private fun gettingAds(category: String?) {

      lifecycleScope.async (Dispatchers.IO) {


          val job=async {
              try {


                  fStore.collection("ads")
                      .whereEqualTo("adType", category)
                      .get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                          override fun onSuccess(querySnapshot: QuerySnapshot?) {
                              for (qds: QueryDocumentSnapshot in querySnapshot!!) {

                                  val adId: String = qds.id.toString()
                                  val displayAdTitle: String = qds.getString("adTitle").toString()
                                  var displayAdPrice: String = qds.getString("adPrice").toString()
                                  var displayAdImage: String =
                                      qds.getString("adImageUrl").toString()
                                  var displayAdDetail: String = qds.getString("adDetail").toString()
                                  var displayAdType: String = qds.getString("adType").toString()
                                  var displayAdUserId: String = qds.getString("adUserId").toString()
                                  var displayAdSearchTitle: String =
                                      qds.getString("adSearchTitle").toString()
                                  var allImagesUrl: String = qds.getString("adAllImages").toString()
                                  var adPhoneNo: String = qds.getString("adPhoneNo").toString()
                                  var adLocation: String = qds.getString("adLocation").toString()


                                  adIds.add(adId)
                                  adList.add(
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
                                          null
                                      ,null, adId)
                                  )
                                  categoryAdsRV.startLayoutAnimation()
                                  categoryAdsRVAdapter.notifyDataSetChanged()

                              }
                          }
                      })
              } catch (e: Exception) {
                  Log.d("err", "")
              }
          }.await()
      }
    }

    private fun callCategoryIcons(category: String?) {
        if (category=="freelancing"){
            viewCategoryAdIocn.setImageResource(R.drawable.categoryfreelance)
        }else if (category=="gaming"){
            viewCategoryAdIocn.setImageResource(R.drawable.categorygaming)
        }else if (category=="instagram"){
            viewCategoryAdIocn.setImageResource(R.drawable.categoryinstagram)
        }else if (category=="facebook"){
            viewCategoryAdIocn.setImageResource(R.drawable.categoryfacebook)
        }else if (category=="youtube"){
            viewCategoryAdIocn.setImageResource(R.drawable.categoryyoutube)
        }else if (category=="influencer"){
            viewCategoryAdIocn.setImageResource(R.drawable.categoryinfluencer)
        }else if (category=="other"){
            viewCategoryAdIocn.setImageResource(R.drawable.categoryother)
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

}