package com.example.enzo

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.withStyledAttributes
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.enzo.Adapters.ViewBidsAdapter
import com.example.enzo.Fragments.MapFrag
import com.example.enzo.Fragments.ViewAdBids
import com.example.enzo.Fragments.ViewBidsFrag
import com.example.enzo.Models.LoginModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController


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
    lateinit var mapFrag:FrameLayout
    lateinit var adLocation:CardView
    lateinit var placeBidBtn:CardView
    lateinit var contactNo:TextView
    lateinit var bidBottomSheetLayout:ConstraintLayout
    lateinit var bidDoneBtn:MaterialButton
    lateinit var bottomSHeetBehaviour:BottomSheetBehavior<ConstraintLayout>
    lateinit var adUploaderProfile:ConstraintLayout
    lateinit var bidStartPrice:TextView
    lateinit var enterBidet:EditText
    lateinit var viewAllBidsBtn:MaterialButton


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
        contactNo=findViewById(R.id.contactNo)
        chatWithUploaderBtn=findViewById(R.id.chatWithUploaderBtn)
        saveAdBtn=findViewById(R.id.saveAdBtn)
        saveIconImg=findViewById(R.id.saveIconImg)
        adUploaderProfile=findViewById(R.id.adUploaderProfile)
        goBackBtn=findViewById(R.id.goBackBtn)
        adLocation=findViewById(R.id.adLocation)
        ///biddingLayout
        placeBidBtn=findViewById(R.id.placeBidBtn)
        bidDoneBtn=findViewById(R.id.bidDoneBtn)
        bidStartPrice=findViewById(R.id.bidStartPrice)
        enterBidet=findViewById(R.id.enterBidet)
        bidBottomSheetLayout=findViewById(R.id.bid_bottom_sheet)
        viewAllBidsBtn=findViewById(R.id.viewAllBidsBtn)

        /*BottomSheetBehavior.from(bidBottomSheetLayout).state=BottomSheetBehavior.STATE_HIDDEN*/
        mapFrag= findViewById(R.id.mapFrag)
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
        val bid=intent.getStringExtra("adViewBid")
        val detail= intent.getStringExtra("adViewDetail")
        val adPhoneNo= intent.getStringExtra("adPhoneNo")
        val idOfUploader=intent.getStringExtra("idOfUploader")
        val adId=intent.getStringExtra("adId")
        val adLocLatitude=intent.getStringExtra("adLocLatitude")
        val adLocLongitude=intent.getStringExtra("adLocLongitude")



try {
    Picasso.get().load(imageUrl).fit().centerCrop().placeholder(R.drawable.gray).into(adViewImage)
    adViewTitle.text = title
    adViewPrice.text = price
    adViewDetail.text = detail
    contactNo.text=adPhoneNo
}catch (e:Exception){
    Log.d("err","")
}


        goBackBtn.setOnClickListener {
            finish()
        }

        adViewImage.setOnClickListener {
            val intent=Intent(this, DisplayAdImages::class.java)

            intent.putExtra("allImagesUrl", allImagesUrl)
            intent.putExtra("titleImgUrl", imageUrl)
            startActivity(intent)
            overridePendingTransition(0,0)

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

                    val user = hashMapOf("idOfUploaderChats" to idOfUploader,
                    "adId" to adId)

                    val sR: DocumentReference = fStore.collection("users")
                        .document(auth.currentUser!!.uid).collection("idOfUploaderChats")
                        .document(idOfUploader.toString())

                    sR.set(user)

                }
                val intent = Intent(this, ChattingScreen::class.java)
                intent.putExtra("idOfWhoseChatClicked", idOfUploader)
                intent.putExtra("adId", adId)
                startActivity(intent)
            }

        }

        adLocation.setOnClickListener {

            if (adLocLatitude.toString()=="null" && adLocLongitude.toString()=="null"){
                Toast.makeText(this, "Location not provided by the seller", Toast.LENGTH_SHORT).show()

            }else {
                val bundle = Bundle()
                bundle.putString("adLocLatitude", adLocLatitude.toString())
                bundle.putString("adLocLongitude", adLocLongitude.toString())

                val mapFragment = MapFrag()

                mapFragment.arguments = bundle

                supportFragmentManager.beginTransaction().replace(R.id.mapFrag, mapFragment)
                    .addToBackStack(null)
                    .commit()
            }

        }

adUploaderProfile.setOnClickListener {
    val i=Intent(this, ProfileActivity::class.java)
    i.putExtra("userId", idOfUploader)
    startActivity(i)

}
        bottomSHeetBehaviour=BottomSheetBehavior.from(bidBottomSheetLayout).apply {
            this.peekHeight=0
        }
        placeBidBtn.setOnClickListener {
            if (idOfUploader.toString()==auth.currentUser?.uid.toString()){
            Toast.makeText(this, "Cannot Bid on your own Ad", Toast.LENGTH_SHORT).show()
            }else if(bid==""){
                Toast.makeText(this, "Bidding not allowed by seller", Toast.LENGTH_SHORT).show()
            }

            else
            bottomSHeetBehaviour.state=BottomSheetBehavior.STATE_EXPANDED

            bidStartPrice.text=bid.toString() +" Rs"


        }
        bidDoneBtn.setOnClickListener {
            val enteredBid:String=enterBidet.text.toString()
            if (enterBidet.text.toString().isEmpty()) {
                Toast.makeText(this, "Enter bid first", Toast.LENGTH_SHORT).show()
            }else {
                var bidInt:Int=1
               try {
                   bidInt = Integer.parseInt(enteredBid)
               }catch (e:Exception){
                   Log.d("","")
               }
                val adBidInt: Int? =bid?.toInt()
                if (bidInt< adBidInt!!){
                    Toast.makeText(this, "Place bid higher than starting bid", Toast.LENGTH_LONG).show()

                }else {
                val bidHashMap = hashMapOf<String, String>(
                    "bid" to enteredBid,
                    "bidderId" to auth.currentUser!!.uid.toString(),
                    "adId" to adId.toString()
                )

    lifecycleScope.launch(Dispatchers.IO) {
        fStore.collection("ads").document(adId.toString())
           .collection("bidding").document().set(bidHashMap).addOnSuccessListener {

                Toast.makeText(this@ViewAdActivity, "Bid placed. Seller will contact you if interested.", Toast.LENGTH_LONG)
                    .show()
                enterBidet.clearFocus()

                bottomSHeetBehaviour.state=BottomSheetBehavior.STATE_HIDDEN



            }.addOnFailureListener {
            Toast.makeText(this@ViewAdActivity, "Couldn't place Bid, Try again.", Toast.LENGTH_SHORT)
                .show()
                bottomSHeetBehaviour.state=BottomSheetBehavior.STATE_HIDDEN
        }
}

            }
                hideKeyboard(this@ViewAdActivity)
        }

        }
///View Bids bottom sheet


        ///viewBids recyclerView
    viewAllBidsBtn.setOnClickListener {
        val bundle = Bundle()
        bundle.putString("adId", adId)

        val viewBidsFrag = ViewAdBids()

        viewBidsFrag.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.mapFrag, viewBidsFrag)
            .addToBackStack(null)
            .commit()

        }

    }//oncreate

    //////to make bottom sheet go down on clicking outside
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (ev?.action === MotionEvent.ACTION_DOWN) {
            if (bottomSHeetBehaviour.state== BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()
               bidBottomSheetLayout.getGlobalVisibleRect(outRect)

                if (!outRect.contains((ev.rawX as Float).toInt(), (ev.rawY as Float).toInt())
                ) bottomSHeetBehaviour.setState(
                    BottomSheetBehavior.STATE_COLLAPSED
                )
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }
}//main