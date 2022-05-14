package com.example.enzo.Repository

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.enzo.Models.AdModel
import com.example.enzo.Models.AllChatsModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class HomeRepository {
    private val fStore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()
    val adIds=ArrayList<String>()
    var allAdsList=ArrayList<AdModel>()

    var  socialMediaAdsList=ArrayList<AdModel>()
    val socialMediaAdIds=ArrayList<String>()

    fun fetchAllAds(liveData: MutableLiveData<ArrayList<AdModel>>) {

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

                        allAdsList.add(
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
                                null,null,
                                adsId
                            )
                        )


                        adIds.add(adsId)
                        liveData.postValue(allAdsList)

                    }

                }
            })

    }
    fun fetchSocialMediaAds(socialMediaLiveData: MutableLiveData<ArrayList<AdModel>>){

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
                                    null,null,
                                    adId

                                )
                            )

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
                                    null,null,
                                    adId
                                )
                            )


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
                                    null,null,
                                    adId
                                )
                            )


                        }else{

                        }


                    }
                    socialMediaLiveData.postValue(socialMediaAdsList)

                }
            })
    }
    }