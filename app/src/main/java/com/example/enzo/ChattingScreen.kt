package com.example.enzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.enzo.Adapters.ChattingAdapter
import com.example.enzo.Models.MessageModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.os.Build
import android.view.View
import com.example.enzo.NotificationWork.DataNotification
import com.example.enzo.NotificationWork.PushNotification
import com.example.enzo.NotificationWork.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson


class ChattingScreen : AppCompatActivity() {
    lateinit var uploaderImgChatting: RoundedImageView
    lateinit var uploaderNameChatting:TextView
    lateinit var auth: FirebaseAuth
    lateinit var fStore:FirebaseFirestore
    lateinit var msgList: ArrayList<MessageModel>
    lateinit var chatRView: RecyclerView
    lateinit var sendTextBtn: CardView
    lateinit var chatText: EditText
    lateinit var tradeBtn:ImageView
    lateinit var idOfAdUploaderSeller:String
    lateinit var goBackBtn:ImageButton
    lateinit var toolBarChatScreen:Toolbar
    lateinit var nameOfOtherUser:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_screen)


FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
/////initializing

        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()

        toolBarChatScreen=findViewById(R.id.toolbarChatScreen)
        toolBarChatScreen.inflateMenu(R.menu.menu_chatting)
        setSupportActionBar(toolBarChatScreen)
        uploaderImgChatting= findViewById(R.id.uploaderImgChatting)
        uploaderNameChatting= findViewById(R.id.uploaderNameChatting)
        sendTextBtn= findViewById(R.id.sendText)
        tradeBtn=findViewById(R.id.tradeBtn)


        ///goBackBtn
        goBackBtn=findViewById(R.id.goBackBtn)
        goBackBtn.setOnClickListener {
            finish()
        }

/////getting uploader img and name from chat frag
        nameOfOtherUser=intent.getStringExtra("nameOfWhoseChatClicked").toString()
        uploaderNameChatting.text=nameOfOtherUser
        Picasso.get().load(intent.getStringExtra("imgOfWhoseChatClicked")).into(uploaderImgChatting)

 //////from view ad activity
        ///id of seller ad uploader
        idOfAdUploaderSeller= intent.getStringExtra("idOfWhoseChatClicked").toString()

      lifecycleScope.launch(Dispatchers.IO) {
          try {


              fStore.collection("users").document(idOfAdUploaderSeller).get().addOnSuccessListener {

                  uploaderNameChatting.text = it.getString("profileName")
                  val uploaderImgChattingUrl: String = it.getString("profileUrl").toString()
                  Picasso.get().load(uploaderImgChattingUrl).into(uploaderImgChatting)
              }
          }catch (r:Exception){
              Log.d("h","")
          }
      }

//////////setting chatting adapter///
        chatRView= findViewById(R.id.chatRView)
        msgList= arrayListOf<MessageModel>()

        chatRView.layoutManager= LinearLayoutManager(applicationContext)
        chatRView.setHasFixedSize(true)

        var chattingAdapter = ChattingAdapter(applicationContext, msgList)
        chatRView.adapter = chattingAdapter



        val senderId= auth.currentUser?.uid.toString()
        val recieverId=idOfAdUploaderSeller
        val senderRoom= senderId+ recieverId
        val recieverRoom= recieverId + senderId

////
      scrollToLastWhenKeyboardIsUp()

////getting messages from firestore
        fStore.collection("chats").document(senderId).collection(senderRoom).orderBy("timeStamp")
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(qs: QuerySnapshot?) {
                    msgList.clear()
                    for (qds: QueryDocumentSnapshot in qs!!) {
                        val msg: String=qds.getString ("messageText").toString()
                        val userId: String=qds.getString("userID").toString()
                        val timeOfText: String= qds.getString("timeOfText").toString()
                        val recieverIde: String= qds.getString("recieverID").toString()
                        val timeStamp: String= qds.getString("timeStamp").toString()

                        chatRView.startLayoutAnimation()
                        msgList.add(MessageModel(userId, msg, timeOfText,recieverIde, timeStamp))
                        //scroll to last
                        chatRView.scrollToPosition(chattingAdapter.getItemCount()-1);

                    }
                    chattingAdapter.notifyDataSetChanged()
                }
            })


///////clicking send message button and uploading msg
        sendTextBtn.setOnClickListener {



             chatText= findViewById(R.id.chatText)
           var chatTextString:String?=  chatText.text.toString()

            if (chatTextString==""){
                Log.d("","")
            }else {
                val currentUserId: String = auth.currentUser?.uid.toString()

                val calendar: Calendar = Calendar.getInstance()
                val format: SimpleDateFormat = SimpleDateFormat("hh:mm a")
                val timeOfText: String = format.format(calendar.time)
                val recieverIde: String = idOfAdUploaderSeller.toString()
//////creating Time stamp
                val tsLong = System.currentTimeMillis()
                val timeStamp = tsLong.toString()
///making object of model MessageModel and adding values to it
                val msgModel: MessageModel =
                    MessageModel(currentUserId, chatTextString, timeOfText, recieverIde, timeStamp)


//////displaying current text and uploading message with with firestore
                msgList.add(
                    MessageModel(
                        currentUserId,
                        chatText.text.toString(),
                        timeOfText,
                        recieverIde,
                        timeStamp
                    )
                )
                chattingAdapter.notifyDataSetChanged()

                val user = hashMapOf(
                    "idOfUploaderChats" to currentUserId)
                val zR: DocumentReference = fStore.collection("users")
                    .document(recieverId).collection("idOfUploaderChats")
                    .document(currentUserId)

                zR.set(user)

                val sR: DocumentReference = fStore.collection("chats")
                    .document(currentUserId).collection(senderRoom).document()

                sR.set(msgModel)

                val rR: DocumentReference = fStore.collection("chats")
                    .document(recieverId.toString()).collection(recieverRoom).document()

                rR.set(msgModel)
                chatText.setText("")

                //scroll to last
                chatRView.scrollToPosition(chattingAdapter.itemCount - 1 )

         ////notification work
                var TOPIC = "/topics/myTopic"
                val title="Hello"
                val msg="Guys"
                  PushNotification(DataNotification(title, msg), TOPIC).also {

                      sendNotification(it)
                  }
            }





        }////sendBtn End
        tradeBtn.setOnClickListener {



            val intent = Intent(this, BuyerOrSeller::class.java)
            intent.putExtra("yourID", auth.currentUser!!.uid.toString())
            intent.putExtra("recieverID", idOfAdUploaderSeller)
            intent.putExtra("nameOfOther",nameOfOtherUser)
            startActivity(intent)



        }

    }///onCreate

    private fun scrollToLastWhenKeyboardIsUp() {
        if (Build.VERSION.SDK_INT >= 11) {
            chatRView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    left: Int, top: Int, right: Int, bottom: Int,
                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
                ) {
                    if (bottom < oldBottom) {
                        chatRView.postDelayed(java.lang.Runnable {
                            chatRView.smoothScrollToPosition(
                                chatRView.getAdapter()!!.getItemCount()
                            )
                        }, 100)
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chatting, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {



        if (item.getItemId() == R.id.trade) {

            val intent = Intent(this, BuyerOrSeller::class.java)
            intent.putExtra("recieverID", idOfAdUploaderSeller)
            intent.putExtra("nameOfOther",nameOfOtherUser)
            startActivity(intent)


    }
        return super.onOptionsItemSelected(item)
}

    private fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch{
try {
    val response=RetrofitInstance.api.postNotification(notification)
    if (response.isSuccessful){
        Log.d("defy","response:${response.toString()}")
    } else{
        Log.d("defy","${response.errorBody().toString()}")
    }

}catch (e:Exception){
    Log.d("","")
}
    }
}//main