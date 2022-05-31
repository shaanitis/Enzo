package com.example.enzo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Models.LoginModel
import com.example.enzo.R
import com.squareup.picasso.Picasso

class TryAdapter(var context: Context,
                 private var userList: ArrayList<LoginModel>?
): RecyclerView.Adapter<TryAdapter.MyViewHolder>()  {

   /* private val userData= arrayListOf<LoginModel>()*/
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): TryAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sample_all_chats_rv, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return userList!!.size
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem= userList!!.get(position)
        holder.userName.text=currentItem.profileName
        Picasso.get().load(currentItem.profileUrl).into(holder.userImg)
    }


  inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userImg: ImageView =itemView.findViewById(R.id.friendImg)
        val userName: TextView = itemView.findViewById(R.id.name)
        val clickListenerView: View?= itemView

    }

}