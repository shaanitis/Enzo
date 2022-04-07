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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_screen)



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
        uploaderNameChatting.text=intent.getStringExtra("nameOfAdUploaderSeller")
        Picasso.get().load(intent.getStringExtra("imgOfAdUploaderSeller")).into(uploaderImgChatting)

 //////from view ad activity
        ///id of seller ad uploader
        idOfAdUploaderSeller= intent.getStringExtra("idOfAdUploaderSeller").toString()
        ///id of buyer who clicked chat
        val idOfBuyerWhoClickedChat= intent.getStringExtra("idOfBuyerWhoClickedChat")

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
                        val timeStmap: String= qds.getString("timeStamp").toString()

                        chatRView.startLayoutAnimation()
                        msgList.add(MessageModel(userId, msg, timeOfText,recieverIde, timeStmap))

                    }
                    chattingAdapter.notifyDataSetChanged()
                }
            })


///////clicking send message button and uploading msg
        sendTextBtn.setOnClickListener {



             chatText= findViewById(R.id.chatText)
           var chatTextString:String?=  chatText.text.toString()

            if (chatTextString==""){
                Toast.makeText(this, "Type message first", Toast.LENGTH_SHORT).show()
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
                    "idOfUploaderChats" to currentUserId,
                    "idOfAdUploaderSeller" to idOfAdUploaderSeller ,
                    "idOfUserWhoClickedChatBuyer" to idOfBuyerWhoClickedChat
                )
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


            }





        }////sendBtn End
        tradeBtn.setOnClickListener {


           fStore.collection("users").document(auth.currentUser?.uid.toString())
               .collection("idOfUploaderChats").document(recieverId).get().addOnSuccessListener {

                   if (auth.currentUser?.uid.toString() == it.getString("idOfAdUploaderSeller")) {
                       val intent = Intent(this, SellerActivity::class.java)
                       intent.putExtra("yourID", auth.currentUser!!.uid.toString())
                       intent.putExtra("recieverID", recieverId)
                       startActivity(intent)
                   } else if (auth.currentUser?.uid.toString() == it.getString("idOfUserWhoClickedChatBuyer")) {
                       val intent = Intent(this, BuyerActivity::class.java)
                       intent.putExtra("yourID", auth.currentUser!!.uid.toString())
                       intent.putExtra("recieverID", recieverId)
                       startActivity(intent)
                   } else {
                       Log.d("h", "h")
                   }





       }
        }

    }///onCreate

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chatting, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {



        if (item.getItemId() == R.id.trade) {

            fStore.collection("users").document(auth.currentUser?.uid.toString())
                .collection("idOfUploaderChats").document(idOfAdUploaderSeller).get().addOnSuccessListener {

                    if (auth.currentUser?.uid.toString() == it.getString("idOfAdUploaderSeller")) {
                        val intent = Intent(this, SellerActivity::class.java)
                        intent.putExtra("yourID", auth.currentUser!!.uid.toString())
                        intent.putExtra("recieverID", idOfAdUploaderSeller)
                        startActivity(intent)
                    } else if (auth.currentUser?.uid.toString() == it.getString("idOfUserWhoClickedChatBuyer")) {
                        val intent = Intent(this, BuyerActivity::class.java)
                        intent.putExtra("yourID", auth.currentUser!!.uid.toString())
                        intent.putExtra("recieverID", idOfAdUploaderSeller)
                        startActivity(intent)
                    } else {
                        Log.d("h", "h")
                    }
        }

    }
        return super.onOptionsItemSelected(item)
}
}//main