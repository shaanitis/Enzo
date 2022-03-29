package com.example.enzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.cardview.widget.CardView
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
import kotlinx.coroutines.GlobalScope
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
    lateinit var recieverID:String
    lateinit var tradeBtn:ImageView
    lateinit var idOfUserChatClicked:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_screen)


        this.supportActionBar?.hide()
/////initializing

        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()

        uploaderImgChatting= findViewById(R.id.uploaderImgChatting)
        uploaderNameChatting= findViewById(R.id.uploaderNameChatting)
        sendTextBtn= findViewById(R.id.sendText)
        tradeBtn=findViewById(R.id.tradeBtn)

/////getting uploader id by same intent from AdView and AllChats activity and displaying it////////
         val nameOfUserChatClicked=intent.getStringExtra("nameOfUserChatClicked").toString()
        uploaderNameChatting.text=nameOfUserChatClicked
        val imgOfUserChatClicked=intent.getStringExtra("imgOfUserChatClicked").toString()
        Picasso.get().load(imgOfUserChatClicked).into(uploaderImgChatting)

        idOfUserChatClicked= intent.getStringExtra("idOfUploaderChatting").toString()
        recieverID= intent.getStringExtra("idOfUploaderChatting").toString()
        fStore.collection("users").document(intent.getStringExtra("idOfUploaderChatting").toString()).get().addOnSuccessListener {
            uploaderNameChatting.text = it.getString("profileName")
            val uploaderImgChattingUrl: String = it.getString("profileUrl").toString()
            Picasso.get().load(uploaderImgChattingUrl).into(uploaderImgChatting)
        }


//////////setting chatting adapter///
        chatRView= findViewById(R.id.chatRView)
        msgList= arrayListOf<MessageModel>()

        chatRView.layoutManager= LinearLayoutManager(applicationContext)
        chatRView.setHasFixedSize(true)

        var chattingAdapter = ChattingAdapter(applicationContext, msgList)
        chatRView.adapter = chattingAdapter


        val senderId= auth.currentUser!!.uid.toString()
        val senderRoom= senderId+ recieverID
        val recieverRoom= recieverID+ senderId



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
                val recieverIde: String = recieverID.toString()
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
                    "idOfUploaderChats" to currentUserId
                )
                val zR: DocumentReference = fStore.collection("users")
                    .document(idOfUserChatClicked).collection("idOfUploaderChats")
                    .document(currentUserId)

                zR.set(user)

                val sR: DocumentReference = fStore.collection("chats")
                    .document(currentUserId).collection(senderRoom).document()

                sR.set(msgModel)

                val rR: DocumentReference = fStore.collection("chats")
                    .document(recieverID.toString()).collection(recieverRoom).document()

                rR.set(msgModel)
                chatText.setText("")


            }





        }////sendBtn End
        tradeBtn.setOnClickListener {
            val intent = Intent(this, BuyerOrSeller::class.java)
            intent.putExtra("yourID", auth.currentUser!!.uid.toString())
            intent.putExtra("recieverID", recieverID)
            startActivity(intent)
        }

    }///onCreate



}//main