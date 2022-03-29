package com.example.enzo.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Models.AdModel
import com.example.enzo.R

class TryAdapter(var context: android.content.Context, private var searchResultList: ArrayList<AdModel>): RecyclerView.Adapter<TryAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TryAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_result_rv_item, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return searchResultList.size
    }

    fun setItems(){}

  inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val adImage: ImageView =itemView.findViewById(R.id.adImageSearchRV)
        val adTitle: TextView = itemView.findViewById(R.id.adTitleSearchRV)
        val adPrice: TextView = itemView.findViewById(R.id.adPriceSearchRV)
        val clickListenerView: View?= itemView

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}