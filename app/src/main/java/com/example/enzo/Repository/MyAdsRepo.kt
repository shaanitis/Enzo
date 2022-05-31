package com.example.enzo.Repository

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.enzo.Models.AdModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class MyAdsRepo {
    private val fStore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()
    var myAdsList=ArrayList<AdModel>()


    fun fetchAllAds(liveData: MutableLiveData<ArrayList<AdModel>>) {

        fStore.collection("ads")
            .whereEqualTo("adUserId", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(qs: QuerySnapshot?) {


                        for (qds: QueryDocumentSnapshot in qs!!) {


                            var adsId: String = qds.id.toString()
                            var displayAdTitle: String =
                                qds.getString("adTitle").toString()
                            var displayAdPrice: String =
                                qds.getString("adPrice").toString()
                            val adBid:String=qds.getString("adBid").toString()
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
                            var attachedImages: String =
                                qds.getString("attachedImages").toString()
                            var adPhoneNo: String =
                                qds.getString("adPhoneNo").toString()
                            var adLocLatitide:String=qds.getString("adLocLatitude").toString()
                            var adLocLongitude:String=qds.getString("adLocLongitude").toString()


                            myAdsList.add(
                                AdModel(
                                    displayAdTitle,
                                    displayAdDetail,
                                    displayAdPrice,
                                    adBid,
                                    displayAdImage,
                                    displayAdType,
                                    displayAdUserId,
                                    adSearchTitle,
                                    adAllImages,attachedImages,
                                    adPhoneNo,adLocLatitide, adLocLongitude, adsId)
                            )

                          liveData.postValue(myAdsList)

                        }



                    }

            })
    }
    }