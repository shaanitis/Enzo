package com.example.enzo.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.enzo.ChattingScreen
import com.example.enzo.Models.AdModel
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.R
import com.example.enzo.ViewAdActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class SearchResultRVAdapter(var context: android.content.Context, private var searchResultList: ArrayList<AdModel>): RecyclerView.Adapter<SearchResultRVAdapter.MyViewHolder>()  {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val fStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_result_rv_item, parent, false)

        return MyViewHolder(itemView)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= searchResultList.get(position)

        Picasso.get().load(currentItem.adImageUrl).placeholder(R.drawable.gray).into(holder.adImage)
        holder.adTitle.text= currentItem.adTitle
        holder.adPrice.text= "Rs ${currentItem.adPrice}"

        holder.clickListenerView?.setOnClickListener {

            val intent= Intent(context, ViewAdActivity::class.java)
            intent.putExtra("adViewImage", currentItem.adImageUrl)
            intent.putExtra("adViewTitle", currentItem.adTitle)
            intent.putExtra("adViewPrice", currentItem.adPrice)
            intent.putExtra("adViewDetail", currentItem.adDetail)
            intent.putExtra("idOfUploader", currentItem.adUserId)
            intent.putExtra("adAllImages", currentItem.adAllImages)
            intent.putExtra("fromSearch", "fromSearch")

            context.startActivity(intent)
        }

        //////on click listener used and ad details sent to next activity//////////
        /*holder.clickListenerView?.setOnClickListener {

            val intent= Intent(context, ChattingScreen::class.java)
            intent.putExtra("idOfUploaderChatting", currentItem.idOfUserChatClicked)
            context.startActivity(intent)
        }
*/



    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val adImage: ImageView =itemView.findViewById(R.id.adImageSearchRV)
        val adTitle: TextView = itemView.findViewById(R.id.adTitleSearchRV)
        val adPrice: TextView = itemView.findViewById(R.id.adPriceSearchRV)
        val clickListenerView: View?= itemView

    }

//////if unable to detect and assign layouts or views, remove the import library .R////////

}