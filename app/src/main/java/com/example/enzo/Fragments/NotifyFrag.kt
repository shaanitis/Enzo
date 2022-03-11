package com.example.enzo.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enzo.R
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class NotifyFrag : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var fStore: FirebaseFirestore
    lateinit var db:FirebaseDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       val view:View= inflater.inflate(R.layout.fragment_notify, container, false)
           auth= FirebaseAuth.getInstance()
        val tryText:EditText=view.findViewById(R.id.tryText)
        val upBtn:Button=view.findViewById(R.id.upBtn)



        upBtn.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("message")
            myRef.setValue("Hello, World!")
            }




        return view
    }

}


