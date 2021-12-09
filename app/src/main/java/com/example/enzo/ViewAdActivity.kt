package com.example.enzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ViewAdActivity : AppCompatActivity() {

////declaring //////
    lateinit var auth:FirebaseAuth
    lateinit var fStore:FirebaseFirestore
    lateinit var adViewImage: ImageView
    lateinit var imageOfUploader:ImageView
    lateinit var nameOfUploader:TextView
    lateinit var adViewTitle:TextView
    lateinit var adViewPrice: TextView
    lateinit var adViewDetail: TextView
    lateinit var chatWithUploaderBtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_ad)
/////hiding status bar/////////
        this.supportActionBar?.hide()
/////initializing//////////
        adViewImage= findViewById(R.id.adViewImage)
        adViewTitle= findViewById(R.id.adViewTitle)
        adViewPrice= findViewById(R.id.adViewPrice)
        adViewDetail= findViewById(R.id.adViewDetail)
        imageOfUploader= findViewById(R.id.imageOfUploader)
        nameOfUploader= findViewById(R.id.nameOfUploader)
        chatWithUploaderBtn=findViewById(R.id.chatWithUploaderBtn)

        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()

////////getting ad details from recycler view adatper in previous activity/fragment////////
        val imageUrl= intent.getStringExtra("adViewImage")
        val title= intent.getStringExtra("adViewTitle")
        val price= intent.getStringExtra("adViewPrice")
        val detail= intent.getStringExtra("adViewDetail")
        val idOfUploader=intent.getStringExtra("idOfUploader")

        Picasso.get().load(imageUrl).into(adViewImage)
        adViewTitle.text=title
        adViewPrice.text= price
        adViewDetail.text= detail

////////////////getting user info from firestore by help of user id/////////
        fStore.collection("users").document(idOfUploader.toString()).get().addOnSuccessListener {
            nameOfUploader.text= it.getString("profileName")
            val picUrl:String= it.getString("profileUrl").toString()
            Picasso.get().load(picUrl).into(imageOfUploader)
        }
//////////on clicking chat button, going to chatting screen and also adding id of this uploader as collection in current user's id
        chatWithUploaderBtn.setOnClickListener {

            val user = hashMapOf(
                "idOfUploaderChats" to idOfUploader
            )
            val sR: DocumentReference = fStore.collection("users")
                .document(auth.currentUser!!.uid).collection("idOfUploaderChats").document(idOfUploader.toString())

            sR.set(user)

            val intent= Intent(this, ChattingScreen::class.java)
            intent.putExtra("idOfUploaderChatting", idOfUploader )
            startActivity(intent)
        }


    }//oncreate
}//main