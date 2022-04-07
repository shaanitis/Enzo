package com.example.enzo.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.Models.LoginModel
import com.example.enzo.Repository.ChatRepository
import com.example.enzo.Repository.TryRepository

class ChatFragViewModel : ViewModel() {
    private val repository= ChatRepository()
    private val _userData= MutableLiveData<ArrayList<AllChatsModel>>()
    val userData: MutableLiveData<ArrayList<AllChatsModel>> = _userData

    fun fetchAds(){
        repository.fetchAds(_userData)
    }

}