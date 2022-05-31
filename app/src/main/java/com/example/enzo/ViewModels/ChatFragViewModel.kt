package com.example.enzo.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.Repository.ChatRepository
import com.facebook.all.All
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class ChatFragViewModel() : ViewModel() {
    val repo=ChatRepository()
    private val fStore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()

    private val _userData: MutableLiveData<ArrayList<AllChatsModel>>
    val userData: LiveData<ArrayList<AllChatsModel>>
    get() = _userData


    init {
        Log.d("viewModel","Init")
        _userData= MutableLiveData()
        _userData.value=fetchAds()

    }
    fun getAds(): LiveData<ArrayList<AllChatsModel>>{
        return _userData
    }
    private fun fetchAds():ArrayList<AllChatsModel>{
        val allChatsList= arrayListOf<AllChatsModel>()
        repo.fetchAds(_userData)

      allChatsList==_userData
        return allChatsList
    }

}

