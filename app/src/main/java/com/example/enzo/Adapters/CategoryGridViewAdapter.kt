package com.example.enzo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.enzo.R

class CategoryGridViewAdapter(val context: Context,
                              var categoryNames: Array<String>,
                              var categorIcons: Array<Int>) : BaseAdapter() {


    override fun getCount(): Int {
       return categorIcons.size
    }

    override fun getItem(position: Int): Any {
       return categorIcons
        return categoryNames
    }

    override fun getItemId(position: Int): Long {

        return categoryNames.indexOf(categoryNames[position]).toLong()
        return categorIcons.indexOf(categorIcons[position]).toLong()

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

           var inflator= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        var convertView=inflator.inflate(R.layout.category_gridview_item, null)


        val categoryImg: ImageView= convertView!!.findViewById(R.id.categoryIcon)
        val categoryText: TextView= convertView!!.findViewById(R.id.categoryText)

        categoryImg.setImageResource(categorIcons[position])
        categoryText.text= categoryNames[position]

    return convertView}

}