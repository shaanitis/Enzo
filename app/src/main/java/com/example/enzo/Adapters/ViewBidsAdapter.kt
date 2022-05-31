package com.example.enzo.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.ChattingScreen
import com.example.enzo.Models.LoginModel
import com.example.enzo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class ViewBidsAdapter (val context:Context,
                       val bidderInfoList:ArrayList<LoginModel>,
                       val allBids:ArrayList<String>,
                       val bidIds:ArrayList<String>,
                       val adId:String,
                       val fromViewAd:Boolean):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val fStore:FirebaseFirestore= FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{

        if (fromViewAd==true){
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_ad_bids_item, parent, false)


            ////////////listener argument here for onclick in activity///////
            return AdViewHolder(itemView)
        }else{
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_bids_item_rv, parent, false)


            ////////////listener argument here for onclick in activity///////
            return MyViewHolder(itemView)
        }


    }


    override fun getItemCount(): Int {
        return bidderInfoList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val bidderImg: CircleImageView = itemView.findViewById(R.id.bidderImg)
        val bidderName: TextView = itemView.findViewById(R.id.bidderName)
        val offeredBid: TextView = itemView.findViewById(R.id.bidPrice)
        val chatWithBidder: CardView = itemView.findViewById(R.id.chatBidderBtn)

        val clickListenerView: View?= itemView

    }
    class AdViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        val bidderImg: CircleImageView = itemView.findViewById(R.id.bidderImg)
        val bidderName: TextView = itemView.findViewById(R.id.bidderName)
        val offeredBid: TextView = itemView.findViewById(R.id.bidPrice)




    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (fromViewAd==false){
            (holder as MyViewHolder)
            try {


                var currentItem = bidderInfoList.get(position)

                holder.bidderName.text = currentItem.profileName
                holder.offeredBid.text = "${allBids[position]}" + " Rs"
                Picasso.get()
                    .load(currentItem.profileUrl).fit().centerCrop()
                    .placeholder(R.drawable.blankuser)
                    .into(holder.bidderImg)


                //////on click listener used and ad details sent to next activity//////////


                holder.chatWithBidder.setOnClickListener {
                    if (currentItem.token == auth.currentUser?.uid) {
                        Toast.makeText(context, "Cannot chat with yourself", Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        val intent = Intent(context, ChattingScreen::class.java)
                        intent.putExtra("idOfWhoseChatClicked", currentItem.token)
                        context.startActivity(intent)
                    }
                }
            }catch (e:Exception){
                Log.d("","")
            }
        }else{
            (holder as AdViewHolder)
            var currentItem = bidderInfoList.get(position)

            holder.bidderName.text = currentItem.profileName
            holder.offeredBid.text = "${allBids[position]}" + " Rs"
            Picasso.get()
                .load(currentItem.profileUrl).fit().centerCrop()
                .placeholder(R.drawable.blankuser)
                .into(holder.bidderImg)

        }
    }
    fun deleteItem(i:Int){



        fStore.collection("ads").document(adId).collection("bidding")
            .document(bidIds[i]).delete().addOnSuccessListener {
                bidderInfoList.removeAt(i)
                allBids.removeAt(i)
                bidIds.removeAt(i)
                notifyDataSetChanged()
            }

}
    }