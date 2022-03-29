package com.example.enzo.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.MyAdsOnClick
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.lang.Exception

class MyAdsAdapter(
    var context: Context, private var searchResultList: ArrayList<AdModel>,
    var adIds: ArrayList<String>, var adAllImgLinkList: ArrayList<String>,
    val onClick: MyAdsOnClick?): RecyclerView.Adapter<MyAdsAdapter.MyViewHolder>()  {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val fStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage= FirebaseStorage.getInstance()
    val alertDialog= AlertDialog.Builder(context)
    lateinit var imgLinks:ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.my_ads_item, parent, false)

        return MyViewHolder(itemView)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= searchResultList.get(position)
        Picasso.get().load(currentItem.adImageUrl).placeholder(R.drawable.gray).into(holder.adImage)
        holder.adTitle.text= currentItem.adTitle
        holder.adPrice.text= "Rs ${currentItem.adPrice}"

        holder.clickListenerView?.setOnClickListener {

         onClick?.onAdItemClick(pos = position, adImage = holder.adImage)
        }


    }


    override fun getItemCount(): Int {
        return searchResultList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val adImage: ImageView = itemView.findViewById(R.id.adImageSearchRV)
        val adTitle: TextView = itemView.findViewById(R.id.adTitleSearchRV)
        val adPrice: TextView = itemView.findViewById(R.id.adPriceSearchRV)
        val clickListenerView: View? = itemView


    }
    ///////////////for swiper gestures or anything/////////
    fun deleteItem(i: Int) {
        try {


            val currentItem = searchResultList.get(i)
            adAllImgLinkList.removeAt(i)
            notifyDataSetChanged()
            fStore.collection("ads").document(adIds[i]).delete().addOnSuccessListener {

                Toast.makeText(context, "Ad deleted Succesfully", Toast.LENGTH_SHORT)
                    .show()
                val storageRef: StorageReference =
                    storage.getReferenceFromUrl(currentItem.adImageUrl.toString())
                storageRef.delete().addOnSuccessListener {

                }

                searchResultList.removeAt(i)
                notifyDataSetChanged()
            }
///deleting storage images
            imgLinks = arrayListOf()
            var noOfImages: Int = 0
            val docRef = fStore.collection("adAllImages").document(adAllImgLinkList[i]).get()
                .addOnSuccessListener {
                    if (it.exists()) {

                        val map: MutableMap<String, Any>? = it.getData()
                        noOfImages = map?.size!!.toInt()

                    } else {

                    }.apply {
                        val noOfImgs = noOfImages

                        fStore.collection("adAllImages").document(adAllImgLinkList[i]).get()
                            .addOnSuccessListener {
                                for (j in 0 until noOfImages) {
                                    val imgLink = it.get(j.toString()).toString()
                                    imgLinks.add(imgLink)

                                }
                                for (j in 0 until imgLinks.size) {

                                    val ref: StorageReference =
                                        storage.getReferenceFromUrl(imgLinks[j])
                                    ref.delete().addOnSuccessListener {

                                    }
                                }


                            }
                    }

                }
 ////////deleting ad from saved ads if saved there
            fStore.collection("savedAds")
                .get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(querySnapshot: QuerySnapshot?) {
                        for (it: QueryDocumentSnapshot in querySnapshot!!){
                            val adId:String= it.id.toString()
                            val userId:String= it.getString("userId").toString()

                            if (userId.contains(auth.currentUser?.uid.toString())){
                                fStore.collection("savedAds").document(adId).delete().addOnSuccessListener {
                                    Log.d("","")
                                }
                            }}}})
////deleting adAllImages doc of ad from firestore
            fStore.collection("adAllImages").document(adAllImgLinkList[i]).delete().addOnSuccessListener {
                Log.d("","")
            }
        }catch (e:Exception){
            Log.e("","")
        }
        }

}