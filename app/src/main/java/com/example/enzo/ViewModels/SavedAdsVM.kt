package com.example.enzo.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.enzo.Models.AdModel
import com.example.enzo.Repository.SavedAdsRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.newFixedThreadPoolContext

class SavedAdsVM:ViewModel() {


    val repo= SavedAdsRepo()
    private val fStore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()

    private val savedAdsData: MutableLiveData<ArrayList<AdModel>>

    private var isRefreshing:MutableLiveData<Boolean>

    private lateinit var loadTrigger:MutableLiveData<Unit>

    init {
        Log.d("viewModel","Init")
        savedAdsData= MutableLiveData()
        savedAdsData.value=fetchSavedAds()

        isRefreshing=MutableLiveData()
        isRefreshing.value=false

     /*   newAdsData= MutableLiveData()
        newAdsData.value=fetchAds()*/

        loadTrigger= MutableLiveData<Unit>()


    }



    fun getSavedAds(): MutableLiveData<ArrayList<AdModel>> {
        Log.d("viewModell", "getSavedAds")

        return savedAdsData
    }
    fun fetchSavedAds():ArrayList<AdModel>{


        Log.d("viewModell", "fetchSavedAds")
        val savedAdsList= arrayListOf<AdModel>()
        repo.fetchAllAds(savedAdsData)

        savedAdsList==savedAdsData
        return savedAdsList
    }
    fun refreshing():LiveData<Boolean>{
        return isRefreshing
    }






}
