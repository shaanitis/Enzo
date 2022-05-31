package com.example.enzo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.Adapters.TryAdapter
import com.example.enzo.Models.AllChatsModel
import com.example.enzo.Models.LoginModel
import com.example.enzo.ViewModels.TryViewModel

class TryActivity : AppCompatActivity() {
lateinit var viewModel:TryViewModel
lateinit var  tryAdapter:TryAdapter
lateinit var tryRV:RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try)
        Log.d("viewModel","Activity OnCreated")
        tryRV=findViewById(R.id.tryRV)

        var listUser= arrayListOf<LoginModel>()


        tryRV.layoutManager= LinearLayoutManager(this)


        viewModel=ViewModelProvider(this)[TryViewModel::class.java]


    viewModel.init()
        tryAdapter=TryAdapter(this, listUser)
        tryRV.adapter=tryAdapter

        viewModel.getTryLiveData().observe(this, Observer {
           listUser.addAll(it)
            tryAdapter.notifyDataSetChanged()
        })

    }




}