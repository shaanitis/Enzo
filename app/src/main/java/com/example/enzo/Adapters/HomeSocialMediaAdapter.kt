package com.example.enzo.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Models.AdModel
import com.example.enzo.OnClickRV.HomeRVOnClick
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class HomeSocialMediaAdapter(
    val context: Context,
    var adList: ArrayList<AdModel>,
    var adIds: ArrayList<String>
):
    RecyclerView.Adapter<HomeSocialMediaAdapter.MyViewHolder>() {

    val fStore= FirebaseFirestore.getInstance()
    val auth= FirebaseAuth.getInstance()
    val savedAdIds:ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ad_rv_item_one, parent, false)


        ////////////listener argument here for onclick in activity///////
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var currentItem = adList.get(position)

        holder.displayAdTitle.text = currentItem.adTitle
        holder.displayAdPrice.text = "${currentItem.adPrice}" + " Rs"
        Picasso.get()
            .load(currentItem.adImageUrl).fit().centerCrop().placeholder(R.drawable.gray)
            .into(holder.displayAdImage)


        //////on click listener used and ad details sent to next activity//////////
        holder.clickListenerView?.setOnClickListener {
             val intent= Intent(context, ViewAdActivity::class.java)

              intent.putExtra("adViewImage", currentItem.adImageUrl)
              intent.putExtra("adViewTitle", currentItem.adTitle)
              intent.putExtra("adViewPrice", currentItem.adPrice)
              intent.putExtra("adViewDetail", currentItem.adDetail)
              intent.putExtra("idOfUploader", currentItem.adUserId)
              intent.putExtra("adAllImages", currentItem.adAllImages)
            intent.putExtra("adLocation", currentItem.adLocation)
            intent.putExtra("adPhoneNo", currentItem.adPhoneNo)
            intent.putExtra("adId", adIds[position])

              context.startActivity(intent)


        }

        holder.saveAdBtn.setOnClickListener {
            Toast.makeText(context, "Ad saved to your list", Toast.LENGTH_SHORT).show()
            holder.saveAdBtn.setImageResource(R.drawable.hearticon)
            val strRef: DocumentReference = fStore.collection("savedAds").document(adIds[position])
            val hash = hashMapOf("userId" to auth.currentUser?.uid.toString())
            strRef.set(hash)

        }


    }
    override fun getItemCount(): Int {
        return adList.size
    }
    ///////////////listener argument here for on click in activity of recycler view//////
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val displayAdTitle: TextView = itemView.findViewById(R.id.displayAdTitle)
        val displayAdPrice: TextView = itemView.findViewById(R.id.displayAdPrice)
        val displayAdImage: ImageView = itemView.findViewById(R.id.displayAdImage)
        val saveAdBtn: ImageButton = itemView.findViewById(R.id.saveAdBtn)

        val clickListenerView: View?= itemView


    }
}