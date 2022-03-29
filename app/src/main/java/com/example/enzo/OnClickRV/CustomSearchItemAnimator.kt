package com.example.enzo.OnClickRV

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.R

class CustomSearchItemAnimator : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        holder?.itemView?.animation=AnimationUtils.loadAnimation(holder?.itemView?.context, R.anim.ad_item_added)
        return super.animateAdd(holder)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        return super.animateRemove(holder)
    }
}