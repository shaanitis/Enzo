package com.example.enzo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.enzo.R
import com.squareup.picasso.Picasso

class ViewPagerImagesAdapter(
    val context: Context,
    var imgLinks: ArrayList<String>,
                              ): PagerAdapter() {
    override fun getCount(): Int {
       return imgLinks.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
       return view==`object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        var inflator= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        var convertView=inflator.inflate(R.layout.all_images_slider_item, null)


        val all_imgs_imgView: ImageView = convertView!!.findViewById(R.id.all_imgs_imgView)

        Picasso.get().load(imgLinks[position]).into(all_imgs_imgView)


        container.addView(convertView, position)
        return convertView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(container)
    }
}