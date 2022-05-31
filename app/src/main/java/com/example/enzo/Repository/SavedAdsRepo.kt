package com.example.enzo.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.enzo.Models.AdModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject

class SavedAdsRepo() {

    private val fStore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()
    var savedAdsList=ArrayList<AdModel>()
    var savedAdIds=ArrayList<String>()

       fun fetchAllAds(liveData: MutableLiveData<ArrayList<AdModel>>) {
           savedAdsList.clear()
           Log.d("viewModel", "repoFirstSavedAds")
           fStore.collection("savedAds")
               .whereEqualTo("userId", auth.currentUser?.uid.toString())
               .get()
               .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                   override fun onSuccess(querySnapshot: QuerySnapshot?) {
                          if (querySnapshot!!.isEmpty){


                          }else {

                              for (it: QueryDocumentSnapshot in querySnapshot!!) {
                                  val adId: String = it.id.toString()

                                  savedAdIds.add(adId)


                              }
                              for (i in 0 until savedAdIds.size) {
                                  fStore.collection("ads").document(savedAdIds[i]).get()
                                      .addOnSuccessListener {


                                          val adId: String = it.id.toString()
                                          val displayAdTitle: String =
                                              it.getString("adTitle").toString()
                                          var displayAdPrice: String =
                                              it.getString("adPrice").toString()
                                          val adBid: String = it.getString("adBid").toString()
                                          var displayAdImage: String =
                                              it.getString("adImageUrl").toString()
                                          var displayAdDetail: String =
                                              it.getString("adDetail").toString()
                                          var displayAdType: String =
                                              it.getString("adType").toString()
                                          var displayAdUserId: String =
                                              it.getString("adUserId").toString()
                                          var displayAdSearchTitle: String =
                                              it.getString("adSearchTitle").toString()
                                          var allImagesUrl: String =
                                              it.getString("adAllImages").toString()
                                          var adPhoneNo: String =
                                              it.getString("adPhoneNo").toString()
                                          var adLocLatitude: String =
                                              it.getString("adLocLatitude").toString()
                                          var adLocLongitude: String =
                                              it.getString("adLocLongitude").toString()


                                          savedAdsList.add(
                                              AdModel(
                                                  adTitle = displayAdTitle,
                                                  adDetail = displayAdDetail,
                                                  adPrice = displayAdPrice,
                                                  adBid,
                                                  adImageUrl = displayAdImage,
                                                  adType = displayAdType,
                                                  adUserId = displayAdUserId,
                                                  adSearchTitle = displayAdSearchTitle,
                                                  adAllImages = allImagesUrl,null,
                                                  adPhoneNo = adPhoneNo,
                                                  adLocLatitude, adLocLongitude, adId
                                              )
                                          )


                                          liveData?.postValue(savedAdsList)


                                      }.addOnFailureListener {
                                      Log.e("hh", "")
                                  }

                              }
                          }

                   }

               })
       }




    }