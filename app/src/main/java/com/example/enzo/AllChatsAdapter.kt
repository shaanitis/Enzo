package com.example.enzo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AllChatsAdapter(var context: android.content.Context, private var allChatsList: ArrayList<AllChatsModel>): RecyclerView.Adapter<AllChatsAdapter.MyViewHolder>()  {

    val auth:FirebaseAuth= FirebaseAuth.getInstance()
    val fStore:FirebaseFirestore= FirebaseFirestore.getInstance()
    val db:FirebaseDatabase= FirebaseDatabase.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.enzo.R.layout.sample_all_chats_rv, parent, false)

        return MyViewHolder(itemView)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= allChatsList.get(position)

        Picasso.get().load(currentItem.imgOfUserChatClicked).placeholder(R.drawable.blankuser).into(holder.image)
        holder.name.text= currentItem.nameOfUserChatClicked
        holder.lastMsg.text= currentItem.lastMessage



        //////on click listener used and ad details sent to next activity//////////
        holder.clickListenerView?.setOnClickListener {

            val intent= Intent(context, ChattingScreen::class.java)
            intent.putExtra("idOfUploaderChatting", currentItem.idOfUserChatClicked)
            context.startActivity(intent)
        }




    }

    override fun getItemCount(): Int {
        return allChatsList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: ImageView =itemView.findViewById(R.id.friendImg)
        val name: TextView = itemView.findViewById(R.id.name)
        val lastMsg: TextView = itemView.findViewById(R.id.lastMsg)
        val clickListenerView: View?= itemView

    }

//////if unable to detect and assign layouts or views, remove the import library .R////////

}