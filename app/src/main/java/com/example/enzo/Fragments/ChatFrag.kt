package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.AllChatsAdapter
import com.example.enzo.ChattingScreen
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.OnClickRV.SwipeGestures
import com.example.enzo.OnClickRV.UserChatOnClick
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.example.enzo.ViewModels.ChatFragViewModel
import com.example.enzo.ViewModels.TryViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ChatFrag : Fragment(), UserChatOnClick {
    lateinit var fStore: FirebaseFirestore
    lateinit var auth:FirebaseAuth
    lateinit var db:FirebaseDatabase
    lateinit  var allChatsRV: RecyclerView
    lateinit var allChatsList: ArrayList<AllChatsModel>
    lateinit var shimmerUserChat:ShimmerFrameLayout
    lateinit var idsOfChats:ArrayList<String>
    lateinit var allChatsAdapter:AllChatsAdapter
    lateinit var viewModel: ChatFragViewModel
    lateinit var chatList:ArrayList<AllChatsModel>
    lateinit var searchNothingImage: ImageView
    lateinit var searchNothingText: TextView
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
        searchNothingImage=view.findViewById(R.id.searchNothingImage)
        searchNothingText=view.findViewById(R.id.searchNothingText)
        searchNothingImage.visibility=View.GONE
        searchNothingText.visibility=View.GONE


        allChatsList= arrayListOf<AllChatsModel>()
        idsOfChats= arrayListOf()
chatList= arrayListOf()

////setting up recycler view adapter
        allChatsRV.layoutManager= LinearLayoutManager(requireContext())
        allChatsRV.setHasFixedSize(true)






      allChatsAdapter= AllChatsAdapter(requireContext(), allChatsList, this)
        allChatsRV.adapter=allChatsAdapter

        ///displaying chats

        allChatsList.clear()


           displayUserChats()

        /////recycler view swipe gestures
       /* val swipeGesture= object : SwipeGestures(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {

                    ItemTouchHelper.LEFT -> {


                        MaterialAlertDialogBuilder(requireContext()).setTitle("Want to delete this chat?")
                            .setMessage("All messages and trading data with the user will be cleared!")
                            .setNegativeButton("No"){dialog, it->
                                allChatsAdapter.notifyDataSetChanged()
                            }
                            .setPositiveButton("Yes, delete"){dialog, it->

                                allChatsAdapter.deleteItem(viewHolder.position)

                                Snackbar.make(allChatsRV, "Ad deleted Succesfully", Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                            .show()
                    }
                }


            }
        }

        val touchHelper= ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(allChatsRV)
*/
///onBackPress
        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_chatFrag_to_homeFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)



        return view
    }

    private fun displayUserChats() {


        ///////getting ids of uploaders with whom user chatted and saving ids in array list idsOfChats
        lifecycleScope.async(Dispatchers.IO) {
                      try {

val job=async {
    fStore.collection("users")
        .document(auth.currentUser!!.uid)
        .collection("idOfUploaderChats")
        .get()
        .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
            override fun onSuccess(qs: QuerySnapshot?) {
                if (qs!!.isEmpty){
                    searchNothingImage.visibility=View.VISIBLE
                    searchNothingText.visibility=View.VISIBLE
                    shimmerUserChat.visibility = View.GONE
                } else{
                for (qds: QueryDocumentSnapshot in qs!!) {
                    val idOfUploaderChats: String =
                        qds.getString("idOfUploaderChats").toString()
                    idsOfChats.add("$idOfUploaderChats")

                }
                }
////////looping through array of ids and getting info of each id and displaying chats overall
                for (i in idsOfChats) {

                    val documentReference: DocumentReference =
                        fStore.collection("users").document(i)
                    documentReference.get().addOnSuccessListener {
                        allChatsRV.startLayoutAnimation()
                        var nameOfUserChatclicked: String =
                            it.getString("profileName").toString()
                        var imgUrlOfUserChatClicked: String =
                            it.getString("profileUrl").toString()
                        var idOfUserChatClicked: String = it.id
                        allChatsList.add(
                            AllChatsModel(
                                nameOfUserChatclicked,
                                imgUrlOfUserChatClicked,
                                idOfUserChatClicked
                            )
                        )
                        shimmerUserChat.visibility = View.GONE
                        allChatsAdapter.notifyDataSetChanged()


                    }

                }

                allChatsAdapter.notifyDataSetChanged()
            }

        })
}
    } catch (e:Exception){
        Log.d("", "")
    }
}
    }


    override fun onAdItemClick(pos: Int, userName:TextView, userImg: ImageView ) {
        val intent= Intent(requireContext(), ChattingScreen::class.java)
        //seller details
        intent.putExtra("idOfAdUploaderSeller", allChatsList[pos].idOfUserChatClicked)
        intent.putExtra("imgOfAdUploaderSeller", allChatsList[pos].imgOfUserChatClicked)
        intent.putExtra("nameOfAdUploaderSeller", allChatsList[pos].nameOfUserChatClicked)
        //buyer id
        intent.putExtra("idOfBuyerWhoClickedChat", auth.currentUser?.uid.toString())

        val p1: Pair<View, String>
        p1= Pair(userName, "userChatNameTrans")

        val p2: Pair<View, String>
        p2= Pair(userImg, "userChatImgTrans")

        val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),p1, p2)
        startActivity(intent, extras.toBundle())

        super.onAdItemClick(pos,userName, userImg)
    }




    fun toGetAllUsersFromFirestore(){
       /* fStore.collection("users")
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

            })*/
    }

}
