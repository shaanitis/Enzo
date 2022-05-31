package com.example.enzo.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.MyAdsOnClick
import com.example.enzo.OnClickRV.OnViewBidClick
import com.example.enzo.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class MyAdsAdapter(
    var context: Context, private var searchResultList: ArrayList<AdModel>,
    val onClick: MyAdsOnClick?,
    val onBidClick: OnViewBidClick
): RecyclerView.Adapter<MyAdsAdapter.MyViewHolder>()  {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val fStore = FirebaseFirestore.getInstance()
    val storage= FirebaseStorage.getInstance()
    lateinit var imgLinks:ArrayList<String>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.my_ads_item, parent, false)

        return MyViewHolder(itemView)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= searchResultList.get(position)
        Picasso.get().load(currentItem.adImageUrl).placeholder(R.drawable.enzo_place_holder).into(holder.adImage)
        holder.adTitle.text= currentItem.adTitle
        holder.adPrice.text= "Rs ${currentItem.adPrice}"

        holder.clickListenerView?.setOnClickListener {

            onClick?.onAdItemClick(pos = position, adImage = holder.adImage)
        }
        holder.adBid.setOnClickListener {
            onBidClick.onBidClick(position, currentItem.adId.toString())
        }



    }


    override fun getItemCount(): Int {
        return searchResultList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val adImage: ImageView = itemView.findViewById(R.id.adImageSearchRV)
        val adTitle: TextView = itemView.findViewById(R.id.adTitleSearchRV)
        val adPrice: TextView = itemView.findViewById(R.id.adPriceSearchRV)
        val adBid: TextView =itemView.findViewById(R.id.myAdBidRV)
        val clickListenerView: View? = itemView


    }
    ///////////////for swiper gestures or anything/////////
    fun deleteItem(i: Int) {


        imgLinks = arrayListOf()

        var noOfImages: Int=0
        val currentItem = searchResultList.get(i)
        searchResultList.removeAt(i)
        notifyDataSetChanged()




        fStore.collection("ads").document(currentItem.adId.toString())
            .delete().addOnSuccessListener {

                val storageRef: StorageReference =
                    storage.getReferenceFromUrl(currentItem.adImageUrl.toString())
                storageRef.delete()



            }
///deleting storage images
        fStore.collection("adAllImages")
            .document(currentItem.adAllImages.toString()).get()
            .addOnSuccessListener {

                if (it.exists()) {
                    val map: MutableMap<String, Any>? = it.getData()
                    noOfImages = map?.size!!.toInt()

                } else {

                }.apply {

                    val noImg=noOfImages

                    fStore.collection("adAllImages")
                        .document(currentItem.adAllImages.toString()).get()
                        .addOnSuccessListener {

                            for (i in 0 until noOfImages) {
                                Toast.makeText(context, i.toString(), Toast.LENGTH_SHORT).show()
                                val imgLink: String = it.get(i.toString()).toString()

                                imgLinks.add(imgLink)

                            }
                            for (j in 0 until imgLinks.size) {
                                val storageReference: StorageReference =
                                    storage.getReferenceFromUrl(imgLinks[j])
                                storageReference.delete().addOnSuccessListener{

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
        fStore.collection("adAllImages").document(currentItem.adAllImages.toString())
            .delete().addOnSuccessListener {

            }
    }
}
