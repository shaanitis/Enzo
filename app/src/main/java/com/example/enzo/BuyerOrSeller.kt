package com.example.enzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class BuyerOrSeller : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_or_seller)

         val auth=FirebaseAuth.getInstance()
    val asBuyerBtn:Button=findViewById(R.id.asBuyerBtn)
        val asSellerBtn:Button=findViewById(R.id.asSellerBtn)

        val yourID:String=intent.getStringExtra("yourID").toString()
        val recieverID:String=intent.getStringExtra("recieverID").toString()



        asBuyerBtn.setOnClickListener {
            val intent= Intent(this, BuyerActivity::class.java)
            intent.putExtra("sellerID", recieverID)
            intent.putExtra("buyerID", yourID)
            startActivity(intent)

        }
        asSellerBtn.setOnClickListener {
            val intent=Intent(this, SellerActivity::class.java)
            intent.putExtra("buyerID", recieverID)
            intent.putExtra("sellerID", yourID)
            startActivity(intent)
        }

    }//oncraete
}//main