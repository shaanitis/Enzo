package com.example.enzo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter

class WelcomeAdapter(var context: Context, var layouts: IntArray) : PagerAdapter() {


    //////////just inflated 4 layout slider xml files, not much work here///////////

    lateinit var inflator: LayoutInflater


    override fun getCount(): Int {
        return layouts.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        inflator= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = inflator.inflate(layouts[position], container, false)

        view.setOnClickListener {
            Toast.makeText(context, "Hi", Toast.LENGTH_SHORT).show()
        }

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }
}