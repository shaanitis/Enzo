package com.example.enzo.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.R

class HomeCategoryAdapter(
    var context: Context,
    var categoryNames: Array<String>,
    var categoryIcons: Array<Int>,
    var categories: Array<String>
): RecyclerView.Adapter<HomeCategoryAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeCategoryAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_category_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.categoryImg.setImageResource(categoryIcons[position])
        holder.categoryText.text=categoryNames[position]


        holder.clickListenerView?.setOnClickListener {
//////to navigate to fragment from adapter class

              val navController: NavController = Navigation.findNavController(holder.clickListenerView)
                navController.navigate(R.id.action_homeFrag_to_viewCategoryAds, Bundle().apply {
                    putString("homeCategory", categories[position])
                        putString("homeCategoryName", categoryNames[position])

                })
        }
    }
    override fun getItemCount(): Int {
        return categoryNames.size
    }

    fun setItems(){}

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val categoryImg: ImageView= itemView.findViewById(R.id.categoryIcon)
        val categoryText: TextView= itemView.findViewById(R.id.categoryText)
        val clickListenerView: View?= itemView

    }


}