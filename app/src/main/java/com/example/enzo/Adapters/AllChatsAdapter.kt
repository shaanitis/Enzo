package com.example.enzo.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.ChattingScreen
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.OnClickRV.UserChatOnClick
import com.example.enzo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AllChatsAdapter(var context: android.content.Context,
                      private var allChatsList: ArrayList<AllChatsModel>,
                      val onCLick:UserChatOnClick?
): RecyclerView.Adapter<AllChatsAdapter.MyViewHolder>()  {

    val auth:FirebaseAuth= FirebaseAuth.getInstance()
    val fStore:FirebaseFirestore= FirebaseFirestore.getInstance()
    val db:FirebaseDatabase= FirebaseDatabase.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sample_all_chats_rv, parent, false)

        return MyViewHolder(itemView)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= allChatsList.get(position)

        Picasso.get().load(currentItem.imgOfUserChatClicked).placeholder(R.drawable.blankuser).into(holder.image)
        holder.name.text= currentItem.nameOfUserChatClicked



        //////on click listener used and ad details sent to next activity//////////
        holder.clickListenerView?.setOnClickListener {
            onCLick?.onAdItemClick(pos = position, userName = holder.name, userImg = holder.image)
        }




    }

    override fun getItemCount(): Int {
        return allChatsList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: RoundedImageView=itemView.findViewById(R.id.friendImg)
        val name: TextView = itemView.findViewById(R.id.name)
        val clickListenerView: View?= itemView

    }

//////if unable to detect and assign layouts or views, remove the import library .R////////

}