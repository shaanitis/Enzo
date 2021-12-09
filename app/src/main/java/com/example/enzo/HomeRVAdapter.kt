package com.example.enzo

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.coroutines.handleCoroutineException

class HomeRVAdapter(val context: android.content.Context,  var adList: ArrayList<AdModel>):
    RecyclerView.Adapter<HomeRVAdapter.MyViewHolder>() {

///////For on click listener in activity of recycler view/////////
  /*  lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: onItemClickListener){
        mListener= listener
    }
*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_rv_item_one, parent, false)


        ////////////listener argument here for onclick in activity///////
        return HomeRVAdapter.MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var currentItem= adList.get(position)

        holder.displayAdTitle.text= currentItem.adTitle
        holder.displayAdPrice.text= "${currentItem.adPrice}"+" Rs"
        Picasso.get().load(currentItem.adImageUrl).into(holder.displayAdImage)

        //////on click listener used and ad details sent to next activity//////////
        holder.clickListenerView?.setOnClickListener {

            val intent= Intent(context, ViewAdActivity::class.java)
            intent.putExtra("adViewImage", currentItem.adImageUrl)
            intent.putExtra("adViewTitle", currentItem.adTitle)
            intent.putExtra("adViewPrice", currentItem.adPrice)
            intent.putExtra("adViewDetail", currentItem.adDetail)
            intent.putExtra("idOfUploader", currentItem.adUserId)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return adList.size
    }
///////////////listener argument here for on click in activity of recycler view//////
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val displayAdTitle: TextView = itemView.findViewById(R.id.displayAdTitle)
        val displayAdPrice: TextView = itemView.findViewById(R.id.displayAdPrice)
        val displayAdImage: ImageView = itemView.findViewById(R.id.displayAdImage)
        val clickListenerView: View?= itemView

//////////For on click in activity of recycler view/////////
    /*init {
        itemView.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }*/
    }
}