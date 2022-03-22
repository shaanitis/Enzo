package com.example.enzo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.enzo.Adapters.ViewPagerImagesAdapter
import com.google.firebase.firestore.FirebaseFirestore


class DisplayAdImages : AppCompatActivity() {
    lateinit var displayAdImagesVP:ViewPager
    lateinit var fStore:FirebaseFirestore
    lateinit var allImagesUrl:String
    lateinit var imgLinks:ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_ad_images)
        this.supportActionBar?.hide()

        displayAdImagesVP=findViewById(R.id.displayImagesVP)
        fStore= FirebaseFirestore.getInstance()
        imgLinks= arrayListOf()
        allImagesUrl= intent.getStringExtra("allImagesUrl").toString()
        val idUploader=intent.getStringExtra("idUploader")
        val adId=intent.getStringExtra("adId")

        var noOfImages: Int=0
        fStore= FirebaseFirestore.getInstance()
        val docRef= fStore.collection("adAllImages").document(allImagesUrl.toString()).get().addOnSuccessListener {
            if(it.exists()) {

                val map: MutableMap<String,Any>?= it.getData()
                noOfImages= map?.size!!.toInt()

            }else{

            }.apply {
               val noOfImgs= noOfImages


                    fStore.collection("adAllImages").document(allImagesUrl).get().addOnSuccessListener {
                        for (i in 0 until noOfImages) {
                            val imgLink = it.get(i.toString()).toString()
                            imgLinks.add(imgLink)


                        }
                       val viewPagerAdapter: ViewPagerImagesAdapter= ViewPagerImagesAdapter(this@DisplayAdImages, imgLinks)
                        displayAdImagesVP.adapter= viewPagerAdapter
                        displayAdImagesVP.offscreenPageLimit= imgLinks.size - 1
                        viewPagerAdapter.notifyDataSetChanged()
                    }




                }

            }

        }


    }


