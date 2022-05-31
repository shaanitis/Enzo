package com.example.enzo.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.enzo.Models.LoginModel
import com.example.enzo.Repository.TryRepository
import kotlin.system.exitProcess

class TryViewModel:ViewModel() {
    private val repository=TryRepository()
    var userLiveData=MutableLiveData<ArrayList<LoginModel>>()

   fun init() {
        if (userLiveData!=null){
         return
        }
      userLiveData= repository.tryAllAds()
    }

    fun getTryLiveData(): LiveData<ArrayList<LoginModel>> {
        return userLiveData
    }

}