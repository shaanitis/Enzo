package com.example.enzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.withStyledAttributes
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

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
    lateinit var chatWithUploaderBtn:CardView
    lateinit var saveAdBtn:CardView
    lateinit var goBackBtn:ImageButton
    lateinit var saveIconImg:ImageView

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
        saveAdBtn=findViewById(R.id.saveAdBtn)
        saveIconImg=findViewById(R.id.saveIconImg)
        goBackBtn=findViewById(R.id.goBackBtn)

        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()

////////getting ad details from recycler view adatper of home and search adapter from activity/fragment////////
////if want to differentiate between intents from df activity,
// add extra intent when moving from that activity to this activity with that activity name and value
//////    and then check here if the value of that activity name matches the activity name
        val allImagesUrl= intent.getStringExtra("adAllImages").toString()
        val imageUrl= intent.getStringExtra("adViewImage")
        val title= intent.getStringExtra("adViewTitle")
        val price= intent.getStringExtra("adViewPrice")
        val detail= intent.getStringExtra("adViewDetail")
        val idOfUploader=intent.getStringExtra("idOfUploader")
        val adId=intent.getStringExtra("adId")


try {
    Picasso.get().load(imageUrl).fit().centerCrop().placeholder(R.drawable.gray).into(adViewImage)
    adViewTitle.text = title
    adViewPrice.text = price
    adViewDetail.text = detail
}catch (e:Exception){
    Log.d("err","")
}


        goBackBtn.setOnClickListener {
            finish()
        }

        adViewImage.setOnClickListener {
            val intent=Intent(this, DisplayAdImages::class.java)

            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            overridePendingTransition(0, 0)
            intent.putExtra("allImagesUrl", allImagesUrl)
            intent.putExtra("titleImgUrl", imageUrl)
            startActivity(intent)
        }
        saveAdBtn.setOnClickListener {

         saveIconImg.setImageResource(R.drawable.save)
            Toast.makeText(this, "Ad saved to your list", Toast.LENGTH_SHORT).show()

            lifecycleScope.launch(Dispatchers.IO) {
                val strRef: DocumentReference =
                    fStore.collection("savedAds").document(adId.toString())
                val hash = hashMapOf("userId" to auth.currentUser?.uid.toString())
                strRef.set(hash)
            }


        }
////////////////getting user info from firestore by help of user id/////////
     lifecycleScope.async(Dispatchers.IO) {
    val job2=async {
        fStore.collection("users").document(idOfUploader.toString()).get().addOnSuccessListener {
            nameOfUploader.text = it.getString("profileName")
            val picUrl: String = it.getString("profileUrl").toString()
            Picasso.get().load(picUrl).placeholder(R.drawable.blankuser).into(imageOfUploader)
        }
    }
     }
//////////on clicking chat button, going to chatting screen and also adding id of this uploader as collection in current user's id
        chatWithUploaderBtn.setOnClickListener {

            if (idOfUploader == auth.currentUser?.uid.toString()) {
                val sb=Snackbar.make(chatWithUploaderBtn, "Cannot chat if its your own Ad", Snackbar.LENGTH_SHORT)
                sb.setAction("Got It"){
                    sb.dismiss()
                }.show()
            } else {

                lifecycleScope.launch(Dispatchers.IO) {

                    val user = hashMapOf("idOfUploaderChats" to idOfUploader)

                    val sR: DocumentReference = fStore.collection("users")
                        .document(auth.currentUser!!.uid).collection("idOfUploaderChats")
                        .document(idOfUploader.toString())

                    sR.set(user)

                }
                val intent = Intent(this, ChattingScreen::class.java)
                intent.putExtra("idOfWhoseChatClicked", idOfUploader)
                startActivity(intent)
            }

        }


    }//oncreate

}//main