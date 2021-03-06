package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.enzo.Adapters.AllChatsAdapter
import com.example.enzo.ChattingScreen
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.OnClickRV.SwipeGestures
import com.example.enzo.OnClickRV.UserChatOnClick
import com.example.enzo.R
import com.example.enzo.ViewModels.ChatFragViewModel
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
import kotlinx.coroutines.*
import java.lang.Exception

class ChatFrag : Fragment(), UserChatOnClick {
    lateinit var fStore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var allChatsRV: RecyclerView
    lateinit var allChatsList: ArrayList<AllChatsModel>
    lateinit var shimmerUserChat: ShimmerFrameLayout
    lateinit var allChatsAdapter: AllChatsAdapter
    lateinit var viewModel: ChatFragViewModel
    lateinit var searchNothingImage: ImageView
    lateinit var searchNothingText: TextView
    lateinit var adId:String
    lateinit var strChats:SwipeRefreshLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view: View = inflater.inflate(R.layout.fragment_chat, container, false)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        db = FirebaseDatabase.getInstance()

        shimmerUserChat = view.findViewById(R.id.shimmerUserChat)
        allChatsRV = view.findViewById(R.id.allChatsRV)
        searchNothingImage = view.findViewById(R.id.searchNothingImage)
        searchNothingText = view.findViewById(R.id.searchNothingText)
        strChats=view.findViewById(R.id.strChats)
        searchNothingImage.visibility = View.GONE
        searchNothingText.visibility = View.GONE


        allChatsList = arrayListOf<AllChatsModel>()

////setting up recycler view adapter
        allChatsRV.layoutManager = LinearLayoutManager(requireContext())
        allChatsRV.setHasFixedSize(true)
       allChatsAdapter = AllChatsAdapter(requireContext(), allChatsList, this)
        allChatsRV.adapter = allChatsAdapter

        ///displaying chats
        allChatsList.clear()

        //////Using VIEWMODEL to Prevent data reloading on fragment change or screen rotation

        viewModel = ViewModelProvider(this)[ChatFragViewModel::class.java]
        viewModel.getAds().observe(viewLifecycleOwner, Observer { userList ->

            allChatsList.clear()
            allChatsList.addAll(userList)
            if (allChatsList.isEmpty()) {


                shimmerUserChat.visibility = View.VISIBLE
                allChatsAdapter.notifyDataSetChanged()

            } else {

                shimmerUserChat.visibility = View.GONE
                allChatsAdapter.notifyDataSetChanged()
            }
        })
   strChats.setOnRefreshListener {
       allChatsList.clear()
       val idsOfChats=ArrayList<String>()
       fStore.collection("users")
           .document(auth.currentUser!!.uid)
           .collection("idOfUploaderChats")
           .get()
           .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
               override fun onSuccess(qs: QuerySnapshot?) {
                   for (qds: QueryDocumentSnapshot in qs!!) {
                       val idOfUploaderChats: String = qds.getString("idOfUploaderChats").toString()
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
                            allChatsAdapter.notifyDataSetChanged()
                       }


                   }

                   Log.d("viewModel","Repository Ran")

               }

           })
       strChats.isRefreshing=false
   }
        /////recycler view swipe gestures
        val swipeGesture = object : SwipeGestures(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {

                    ItemTouchHelper.LEFT -> {


                        MaterialAlertDialogBuilder(requireContext()).setTitle("Want to delete this chat?")
                            .setMessage("All messages and trading data with the user will be cleared!")
                            .setNegativeButton("No") { dialog, it ->
                                allChatsAdapter.notifyDataSetChanged()
                            }
                            .setPositiveButton("Yes, delete") { dialog, it ->

                                allChatsAdapter.deleteItem(viewHolder.position)
                                allChatsAdapter.notifyDataSetChanged()
                                val sb = Snackbar.make(
                                    allChatsRV,
                                    "Chat deleted Succesfully",
                                    Snackbar.LENGTH_SHORT
                                )
                                sb.setAction("Got It") {
                                    sb.dismiss()
                                }.show()
                            }
                            .show()
                    }
                }


            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(allChatsRV)


///onBackPress
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_chatFrag_to_homeFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)



        return view
    }

    override fun onAdItemClick(pos: Int, userName:TextView, userImg: ImageView ) {



         val intent = Intent(requireContext(), ChattingScreen::class.java)
         //seller details
         intent.putExtra("idOfWhoseChatClicked", allChatsList[pos].idOfUserChatClicked)
         intent.putExtra("imgOfWhoseChatClicked", allChatsList[pos].imgOfUserChatClicked)
         intent.putExtra("nameOfWhoseChatClicked", allChatsList[pos].nameOfUserChatClicked)
        intent.putExtra("fromChatFrag", true)
         //buyer id
         intent.putExtra("idOfBuyerWhoClickedChat", auth.currentUser?.uid.toString())
         /*  intent.putExtra("adId", adId)*/
         val p1: Pair<View, String>
         p1 = Pair(userName, "userChatNameTrans")

         val p2: Pair<View, String>
         p2 = Pair(userImg, "userChatImgTrans")

         val extras = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p1, p2)
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
