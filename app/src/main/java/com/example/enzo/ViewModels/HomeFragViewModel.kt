package com.example.enzo.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.enzo.Models.AdModel
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.Repository.ChatRepository
import com.example.enzo.Repository.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragViewModel:ViewModel() {

    val repo= HomeRepository()
    private val fStore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()

    private val _userData: MutableLiveData<ArrayList<AdModel>>
    private var socialMediaData: MutableLiveData<ArrayList<AdModel>>
    /* val userData: LiveData<ArrayList<AllChatsModel>>
     get() = _userData*/


    init {
        Log.d("viewModel","Init")
        _userData= MutableLiveData()
        _userData.value=fetchAllAds()

        socialMediaData= MutableLiveData()
        socialMediaData.value=fetchSocialMediaAds()


    }
    fun getAllAds(): LiveData<ArrayList<AdModel>> {
        return _userData
    }
    private fun fetchAllAds():ArrayList<AdModel>{
        val allChatsList= arrayListOf<AdModel>()
        repo.fetchAllAds(_userData)

        allChatsList==_userData
        return allChatsList
    }

    fun getSocialMediaAds(): LiveData<ArrayList<AdModel>> {
        return socialMediaData
    }
    private fun fetchSocialMediaAds():ArrayList<AdModel>{
        val socialMediaAdList= arrayListOf<AdModel>()
        repo.fetchSocialMediaAds(socialMediaData)

       socialMediaAdList==socialMediaData
        return socialMediaAdList
    }

}