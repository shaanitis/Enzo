package com.example.enzo.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.enzo.Models.AdModel
import com.example.enzo.Repository.MyAdsRepo
import com.example.enzo.Repository.SavedAdsRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyAdsVM:ViewModel() {
    val repo= MyAdsRepo()
    private val fStore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()

    private val myAdsData: MutableLiveData<ArrayList<AdModel>>


    init {
        Log.d("viewModel","Init")
        myAdsData= MutableLiveData()
        myAdsData.value=fetchMyAds()


    }
    fun getMyAds(): LiveData<ArrayList<AdModel>> {
        return myAdsData
    }
    private fun fetchMyAds():ArrayList<AdModel>{
        val myAdsList= arrayListOf<AdModel>()
        repo.fetchAllAds(myAdsData)

        myAdsList==myAdsData
        return myAdsList
    }


}