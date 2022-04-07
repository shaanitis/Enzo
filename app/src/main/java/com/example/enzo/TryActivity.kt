package com.example.enzo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.HomeRVAdapter
import com.example.enzo.Adapters.TryAdapter
import com.example.enzo.Models.AdModel
import com.example.enzo.Models.LoginModel
import com.example.enzo.ViewModels.TryViewModel
import com.google.android.material.snackbar.Snackbar

class TryActivity : AppCompatActivity() {
lateinit var viewModel:TryViewModel
lateinit var  tryAdapter:TryAdapter
lateinit var tryRV:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try)


        viewModel=ViewModelProvider(this)[TryViewModel::class.java]


        tryRV=findViewById(R.id.tryRV)

        tryRV.layoutManager= LinearLayoutManager(this)
        tryRV.setHasFixedSize(true)




        viewModel.fetchAds()
        viewModel.userData.observe(this) { userList ->


              val tryAdapter= TryAdapter(this, userList)
              tryRV.adapter=tryAdapter

              /*tryAdapter.setItems(userList)*/
              tryAdapter.notifyDataSetChanged()
              Snackbar.make(tryRV, "This", Snackbar.LENGTH_SHORT).show()



    }


    }




}