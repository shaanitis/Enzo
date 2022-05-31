package com.example.enzo.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.enzo.Models.LoginModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlin.coroutines.coroutineContext

class TryRepository() {

    var chatList=ArrayList<LoginModel>()
    var chatLiveData=MutableLiveData<ArrayList<LoginModel>>()

    private val fStore=FirebaseFirestore.getInstance()
    private val auth=FirebaseAuth.getInstance()



fun tryAllAds():MutableLiveData<ArrayList<LoginModel>>{
    if (chatList.size==0){
        loadAds()
    }
    chatLiveData.value=chatList
    return chatLiveData
}
    fun loadAds(){

        fStore.collection("users")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(qs: QuerySnapshot?) {

                    for (qds: QueryDocumentSnapshot in qs!!) {

                        val profileName=qds.get("profileName").toString()
                        val profileUrl=qds.get("profileUrl").toString()
                        chatList.add(LoginModel(profileName, profileUrl))
                        chatLiveData.postValue(chatList)

                    }
                }


            }
            )
    }



/*
   val allChats:LiveData<List<LoginModel>>

   init {

       allChats=fetchAds()
   }*/
  /* fun fetchAds():LiveData<List<LoginModel>>{
       val userList: MutableLiveData<List<LoginModel>>

        fStore.collection("users")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(qs: QuerySnapshot?) {

                    for (qds: QueryDocumentSnapshot in qs!!) {
                        userList.postValue(qds.toObject(LoginModel::class.java))

                    }
                }


                    }
            )

    }*/

}