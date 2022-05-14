package com.example.enzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.enzo.Adapters.SavedAdsAdapter
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.SavedAdsOnClick
import com.example.enzo.databinding.ActivityProfileBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


class ProfileActivity : AppCompatActivity(),SavedAdsOnClick {
    private lateinit var binding:ActivityProfileBinding
    lateinit var fStore:FirebaseFirestore
    lateinit var auth:FirebaseAuth
    lateinit var userId:String
    lateinit var userAllAdsAdapter:SavedAdsAdapter
    lateinit var userAdsList:ArrayList<AdModel>
    lateinit var adIds:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fStore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
       userAdsList= arrayListOf()
        adIds= arrayListOf()

        userId=intent.getStringExtra("userId").toString()
        fStore.collection("users").document(userId)
            .get().addOnSuccessListener {
                val profilePic:String= it.get("profileUrl").toString()
                val profileName:String=it.get("profileName").toString()

                Glide.with(this).load(profilePic).placeholder(R.drawable.blankuser).into(binding.profileActivityPic)
                binding.profileActivityName.text=profileName
            }

        fStore.collection("ads").whereEqualTo("adUserId", userId)
            .get().addOnSuccessListener(object :OnSuccessListener<QuerySnapshot>{
                override fun onSuccess(qs: QuerySnapshot?) {
                    if (qs!!.isEmpty){
                        binding.searchNothingImage.visibility=View.VISIBLE
                        binding.searchNothingText.visibility=View.VISIBLE
                    }else {
                        for (qds: QueryDocumentSnapshot in qs!!) {
                            val adId: String = qds.id.toString()
                            val displayAdTitle: String = qds.getString("adTitle").toString()
                            var displayAdPrice: String = qds.getString("adPrice").toString()
                            var displayAdImage: String = qds.getString("adImageUrl").toString()
                            var displayAdDetail: String = qds.getString("adDetail").toString()
                            var displayAdType: String = qds.getString("adType").toString()
                            var displayAdUserId: String = qds.getString("adUserId").toString()
                            var displayAdSearchTitle: String =
                                qds.getString("adSearchTitle").toString()
                            var allImagesUrl: String = qds.getString("adAllImages").toString()
                            var adPhoneNo: String = qds.getString("adPhoneNo").toString()
                            var adLocation: String = qds.getString("adLocation").toString()

                            userAdsList.add(
                                AdModel(
                                    adTitle = displayAdTitle,
                                    adDetail = displayAdDetail,
                                    adPrice = displayAdPrice,
                                    adImageUrl = displayAdImage,
                                    adType = displayAdType,
                                    adUserId = displayAdUserId,
                                    adSearchTitle = displayAdSearchTitle,
                                    adAllImages = allImagesUrl,
                                    adPhoneNo,
                                    null
                                ,null,adId)
                            )
                            adIds.add(adId)
                            userAllAdsAdapter.notifyDataSetChanged()
                        }
                    }
                }

            })
        binding.userAllAds.layoutManager= LinearLayoutManager(this)
        binding.userAllAds.setHasFixedSize(true)

        userAllAdsAdapter= SavedAdsAdapter(this, userAdsList, this)
        binding.userAllAds.adapter=userAllAdsAdapter


    }
    override fun onAdItemClick(pos: Int, adImage: ImageView) {
        val intent= Intent(this, ViewAdActivity::class.java)
        intent.putExtra("adViewImage", userAdsList[pos].adImageUrl)
        intent.putExtra("adViewTitle", userAdsList[pos].adTitle)
        intent.putExtra("adViewPrice", userAdsList[pos].adPrice)
        intent.putExtra("adViewDetail", userAdsList[pos].adDetail)
        intent.putExtra("idOfUploader", userAdsList[pos].adUserId)
        intent.putExtra("adAllImages", userAdsList[pos].adAllImages)
        intent.putExtra("adLocLatitude", userAdsList[pos].adLocLatitude)
        intent.putExtra("adLocLongitude", userAdsList[pos].adLocLongitude)
        intent.putExtra("adPhoneNo",  userAdsList[pos].adPhoneNo)
        intent.putExtra("adId", adIds[pos])


///to send image as shared element
          val p: Pair<View, String>
          p= Pair(adImage, "viewAdImgTrans")
        val extras= ActivityOptionsCompat.makeSceneTransitionAnimation(this, p)
    startActivity(intent, extras.toBundle())

        super.onAdItemClick(pos, adImage)
    }

}