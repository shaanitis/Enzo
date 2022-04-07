package com.example.enzo.Repository

import androidx.lifecycle.MutableLiveData
import com.example.enzo.Models.LoginModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class TryRepository() {
   private val fStore=FirebaseFirestore.getInstance()
    private val auth=FirebaseAuth.getInstance()
    val userList= ArrayList<LoginModel>()

    fun fetchAds(liveData: MutableLiveData<ArrayList<LoginModel>>) {


        fStore.collection("users")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(qs: QuerySnapshot?) {

                    for (qds: QueryDocumentSnapshot in qs!!) {
                       userList.add(qds.toObject(LoginModel::class.java))

                        liveData.postValue(userList)
                    }
                }



                    }
            )

    }

}