package com.example.enzo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.enzo.Adapters.ViewPagerImagesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import android.view.WindowManager

import android.os.Build
import android.view.Window
import android.widget.ImageButton


class DisplayAdImages : AppCompatActivity() {
    lateinit var displayAdImagesVP:ViewPager
    lateinit var fStore:FirebaseFirestore
    lateinit var allImagesUrl:String
    lateinit var titleImgUrl:String
    lateinit var imgLinks:ArrayList<String>
    lateinit var goBackBtn: ImageButton
    var noOfImages: Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_ad_images)
        try {


            this.supportActionBar?.hide()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.setStatusBarColor(Color.BLACK)
            }
        }catch (e:Exception){
            Log.d("","")
        }

goBackBtn=findViewById(R.id.goBackBtn)
        goBackBtn.setOnClickListener {
            finish()
        }

        displayAdImagesVP=findViewById(R.id.displayImagesVP)
        fStore= FirebaseFirestore.getInstance()
        imgLinks= arrayListOf()
        allImagesUrl= intent.getStringExtra("allImagesUrl").toString()
        titleImgUrl= intent.getStringExtra("titleImgUrl").toString()

     try {
         imgLinks.add(titleImgUrl)

     } catch (e:Exception){
         Log.e("", e.message.toString())
     }




  displayAllImages()

        }

    private fun displayAllImages() {
      lifecycleScope.launch(Dispatchers.IO) {
          try {


              val docRef = fStore.collection("adAllImages").document(allImagesUrl.toString()).get()
                  .addOnSuccessListener {
                      if (it.exists()) {

                          val map: MutableMap<String, Any>? = it.getData()
                          noOfImages = map?.size!!.toInt()

                      } else {

                      }.apply {
                          val noOfImgs = noOfImages


                          fStore.collection("adAllImages").document(allImagesUrl).get()
                              .addOnSuccessListener {
                                  for (i in 0 until noOfImages) {
                                      val imgLink = it.get(i.toString()).toString()
                                      imgLinks.add(imgLink)


                                  }
                                  val viewPagerAdapter: ViewPagerImagesAdapter =
                                      ViewPagerImagesAdapter(this@DisplayAdImages, imgLinks)
                                  displayAdImagesVP.adapter = viewPagerAdapter
                                  displayAdImagesVP.offscreenPageLimit = imgLinks.size - 1
                                  viewPagerAdapter.notifyDataSetChanged()
                              }


                      }

                  }
          } catch (e: Exception) {
              Log.e("", e.message.toString())
          }
      }
    }


}



