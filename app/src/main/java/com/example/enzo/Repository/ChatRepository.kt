package com.example.enzo.Repository

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.enzo.Models.AllChatsModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.coroutines.coroutineContext

class ChatRepository() {
    private val fStore= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()
    val idsOfChats=ArrayList<String>()
    var allChatsList=ArrayList<AllChatsModel>()

    fun fetchAds(liveData: MutableLiveData<ArrayList<AllChatsModel>>) {


            fStore.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("idOfUploaderChats")
                .get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(qs: QuerySnapshot?) {
                        for (qds: QueryDocumentSnapshot in qs!!) {
                            val idOfUploaderChats: String =
                                qds.getString("idOfUploaderChats").toString()
                            idsOfChats.add(idOfUploaderChats)

                        }
////////looping through array of ids and getting info of each id and displaying chats overall
                        for (i in idsOfChats) {

                            val documentReference: DocumentReference =
                                fStore.collection("users").document(i)
                            documentReference.get().addOnSuccessListener {

                                var nameOfUserChatclicked: String =
                                    it.getString("profileName").toString()
                                var imgUrlOfUserChatClicked: String =
                                    it.getString("profileUrl").toString()
                                var idOfUserChatClicked: String = it.id
                                allChatsList.add(AllChatsModel(nameOfUserChatclicked, imgUrlOfUserChatClicked,
                                        idOfUserChatClicked
                                    )
                                )

                                liveData.postValue(allChatsList)
                            }


                        }

                    }

                })

        }

}