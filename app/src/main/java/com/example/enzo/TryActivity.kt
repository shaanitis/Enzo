package com.example.enzo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.TryAdapter
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.ViewModels.ChatFragViewModel

class TryActivity : AppCompatActivity() {
lateinit var viewModel:ChatFragViewModel
lateinit var  tryAdapter:TryAdapter
lateinit var tryRV:RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try)
        Log.d("viewModel","Activity OnCreated")
        tryRV=findViewById(R.id.tryRV)

        var listUser= arrayListOf<AllChatsModel>()

        tryAdapter=TryAdapter(this, listUser)
        tryRV.adapter=tryAdapter


        tryRV.layoutManager= LinearLayoutManager(this)


        viewModel=ViewModelProvider(this)[ChatFragViewModel::class.java]


        viewModel.getAds().observe(this, Observer  { userList ->

            Log.d("viewModel","Recieved Ads")
            listUser.clear()
            listUser.addAll(userList)
            tryAdapter.notifyDataSetChanged()

    })


    }




}