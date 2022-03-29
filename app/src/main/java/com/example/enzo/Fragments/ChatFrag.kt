package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.AllChatsAdapter
import com.example.enzo.ChattingScreen
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.OnClickRV.UserChatOnClick
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class ChatFrag : Fragment(), UserChatOnClick {
    lateinit var fStore: FirebaseFirestore
    lateinit var auth:FirebaseAuth
    lateinit var db:FirebaseDatabase
    lateinit  var allChatsRV: RecyclerView
    lateinit var allChatsList: ArrayList<AllChatsModel>
    lateinit var shimmerUserChat:ShimmerFrameLayout

lateinit var testText: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view:View= inflater.inflate(R.layout.fragment_chat, container, false)
      auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        db= FirebaseDatabase.getInstance()
shimmerUserChat=view.findViewById(R.id.shimmerUserChat)

        allChatsRV= view.findViewById(R.id.allChatsRV)
        allChatsList= arrayListOf<AllChatsModel>()
        var idsOfChats= ArrayList<String>()

        allChatsRV.layoutManager= LinearLayoutManager(requireContext())
        allChatsRV.setHasFixedSize(true)


        val allChatsAdapter: AllChatsAdapter = AllChatsAdapter(requireContext(), allChatsList, this)
        allChatsRV.adapter=allChatsAdapter

        allChatsList.clear()




///////getting ids of uploaders with whom user chatted and saving ids in array list idsOfChats
        fStore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("idOfUploaderChats")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot>{
                override fun onSuccess(qs: QuerySnapshot?) {
                    for (qds: QueryDocumentSnapshot in qs!!) {
                        val idOfUploaderChats: String = qds.getString("idOfUploaderChats").toString()
                        idsOfChats.add("$idOfUploaderChats")


                    }
////////looping through array of ids and getting info of each id and displaying chats overall
for (i in idsOfChats) {

    val documentReference: DocumentReference= fStore.collection("users").document(i)
        documentReference.get()
        .addOnSuccessListener{
                     allChatsRV.startLayoutAnimation()
                    var nameOfUserChatclicked:String= it.getString("profileName").toString()
                    var imgUrlOfUserChatClicked:String= it.getString("profileUrl").toString()
                    var idOfUserChatClicked:String=it.id
                    var lastMsg:String=""
                    allChatsList.add(AllChatsModel(nameOfUserChatclicked, imgUrlOfUserChatClicked,lastMsg, idOfUserChatClicked))

            allChatsAdapter.notifyDataSetChanged()
            shimmerUserChat.stopShimmer()
            shimmerUserChat.hideShimmer()
            shimmerUserChat.visibility=View.GONE

                }

            }


}

            })
        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_chatFrag_to_homeFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
////////////getting all users in database
/*
        fStore.collection("users")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot>{
                override fun onSuccess(qs: QuerySnapshot?) {

                    for(qds:QueryDocumentSnapshot in qs!!){
                        var nameOfUserChatclicked:String= qds.getString("profileName").toString()
                        var imgUrlOfUserChatClicked:String= qds.getString("profileUrl").toString()
                        var idOfUserChatClicked:String=qds.id
                        var lastMsg:String=""
                        Toast.makeText(requireContext(), "$nameOfUserChatclicked", Toast.LENGTH_SHORT).show()
                        allChatsList.add(AllChatsModel(nameOfUserChatclicked, imgUrlOfUserChatClicked,lastMsg, idOfUserChatClicked))

                    }
                    allChatsAdapter.notifyDataSetChanged()
                }

            })

*/



/////////checking if document fields contains some specific text and displaying it//////
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
    override fun onAdItemClick(pos: Int, userName:TextView, userImg: ImageView ) {
        val intent= Intent(requireContext(), ChattingScreen::class.java)
        intent.putExtra("idOfUploaderChatting", allChatsList[pos].idOfUserChatClicked)
        intent.putExtra("imgOfUserChatClicked", allChatsList[pos].imgOfUserChatClicked)
        intent.putExtra("nameOfUserChatClicked", allChatsList[pos].nameOfUserChatClicked)


        val p1: Pair<View, String>
        p1= Pair(userName, "userChatNameTrans")

        val p2: Pair<View, String>
        p2= Pair(userImg, "userChatImgTrans")

        val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),p1, p2)
        startActivity(intent, extras.toBundle())

        super.onAdItemClick(pos,userName, userImg)
    }

}
