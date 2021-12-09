package com.example.enzo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class ChatFrag : Fragment() {
    lateinit var fStore: FirebaseFirestore
    lateinit var auth:FirebaseAuth
    lateinit var db:FirebaseDatabase
    lateinit  var allChatsRV: RecyclerView
    lateinit var allChatsList: ArrayList<AllChatsModel>
lateinit var testText: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view:View= inflater.inflate(R.layout.fragment_chat, container, false)
      auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        db= FirebaseDatabase.getInstance()


        allChatsRV= view.findViewById(R.id.allChatsRV)
        allChatsList= arrayListOf<AllChatsModel>()

        allChatsRV.layoutManager= LinearLayoutManager(requireContext())
        allChatsRV.setHasFixedSize(true)


        val allChatsAdapter: AllChatsAdapter= AllChatsAdapter(requireContext(), allChatsList)
        allChatsRV.adapter=allChatsAdapter

        allChatsList.clear()

        fStore.collection("users")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot>{
                override fun onSuccess(qs: QuerySnapshot?) {

                    for(qds:QueryDocumentSnapshot in qs!!){
                        var nameOfUserChatclicked:String= qds.getString("profileName").toString()
                        var imgUrlOfUserChatClicked:String= qds.getString("profileUrl").toString()
                        var idOfUserChatClicked:String=qds.id
                        var lastMsg:String=""
                        allChatsList.add(AllChatsModel(nameOfUserChatclicked, imgUrlOfUserChatClicked,lastMsg, idOfUserChatClicked))


                    }
                    allChatsAdapter.notifyDataSetChanged()
                }

            })





/////////checking if document contains some text and displaying it//////
     /*   testText=view.findViewById(R.id.check)
        val userId= auth.currentUser?.uid
        fStore.collection("ads")
            .whereEqualTo("adUserId", "$userId")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(querySnapshot: QuerySnapshot?) {
                    for (qds: QueryDocumentSnapshot in querySnapshot!!){
                        testText.append("  "+qds.getString("adTitle")+"  : "+qds.getString("adDetail")+"\n\n")
                    }
                }

            })
*/

        return view
    }


}
