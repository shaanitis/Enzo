package com.example.enzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.w3c.dom.Text

class BuyerOrSeller : AppCompatActivity() {
    lateinit var buyerBtn:TextView
     lateinit var sellerBtn:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_or_seller)


    buyerBtn=findViewById(R.id.buyerBtn)
        sellerBtn=findViewById(R.id.sellerBtn)


       val recieverId:String= intent.getStringExtra("recieverID").toString()
       val recieverName:String=intent.getStringExtra("nameOfOther").toString()

        buyerBtn.setOnClickListener {
            val intent = Intent(this, BuyerActivity::class.java)
            intent.putExtra("recieverID", recieverId)
            intent.putExtra("nameOfOther",recieverName)
            startActivity(intent)

        }
        sellerBtn.setOnClickListener {
            val intent = Intent(this, SellerActivity::class.java)
            intent.putExtra("recieverID", recieverId)
            intent.putExtra("nameOfOther",recieverName)
            startActivity(intent)
        }




    }
}