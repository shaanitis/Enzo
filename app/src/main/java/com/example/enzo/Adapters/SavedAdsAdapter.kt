package com.example.enzo.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Fragments.SavedAdsFrag
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.SavedAdsOnClick
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.lang.Exception

class SavedAdsAdapter(
    var context: Context,
    private var adList: ArrayList<AdModel>,
    val onCLick: SavedAdsOnClick
): RecyclerView.Adapter<SavedAdsAdapter.MyViewHolder>()  {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val fStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.saved_ads_item, parent, false)

        return MyViewHolder(itemView)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= adList.get(position)

        Picasso.get().load(currentItem.adImageUrl).placeholder(R.drawable.enzo_place_holder).into(holder.adImage)
        holder.adTitle.text= currentItem.adTitle
        holder.adPrice.text= "Rs ${currentItem.adPrice}"

        holder.clickListenerView?.setOnClickListener {

           onCLick?.onAdItemClick(pos = position, adImage = holder.adImage)
        }





    }
    override fun getItemCount(): Int {
        return adList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val adImage: ImageView =itemView.findViewById(R.id.adImageSearchRV)
        val adTitle: TextView = itemView.findViewById(R.id.adTitleSearchRV)
        val adPrice: TextView = itemView.findViewById(R.id.adPriceSearchRV)
        val clickListenerView: View?= itemView

    }

    fun add(savedAds:ArrayList<AdModel>){
        adList.addAll(savedAds)
        notifyDataSetChanged()
    }
    fun deleteItem(i: Int){

        try {
            val currentItem= adList.get(i)
            adList.removeAt(i)
            notifyDataSetChanged()
        fStore.collection("savedAds").document(currentItem.adId.toString()).delete().addOnSuccessListener {



}
        }catch (e:Exception){
    Log.e("", "")
}
        }

//////if unable to detect and assign layouts or views, remove the import library .R////////

}