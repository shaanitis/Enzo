package com.example.enzo.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.enzo.Models.LoginModel
import com.example.enzo.Repository.TryRepository

class TryViewModel:ViewModel() {
    private val repository=TryRepository()
    private val _userData= MutableLiveData<ArrayList<LoginModel>>()
    val userData:MutableLiveData<ArrayList<LoginModel>> = _userData

    fun fetchAds(){
       repository.fetchAds(_userData)
   }

}