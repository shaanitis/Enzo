package com.example.enzo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.enzo.Models.MessageModel
import com.example.enzo.databinding.ActivityBuyerBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.JsonObject
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject
import android.content.Intent
import android.os.Handler
import androidx.core.os.HandlerCompat
import androidx.core.os.HandlerCompat.postDelayed


class BuyerActivity : AppCompatActivity() {

    lateinit var buyerBind:ActivityBuyerBinding
    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String
    lateinit var cardLoader:ProgressBar
     var customerId:String=""
     var ephericalKey:String=""
    var clientSecret:String=""

    var PUBLISH_KEY:String="pk_test_51KyUN7HLyCan9nQSfCpFveFwu4WuGjSz9NqKCJqOebzPk8DNou1rWuTE8ackIfOKvXGUUIlEGn9lkA4R8MB3spb200HQrk9hyg"
    var SECRET_KEY:String="sk_test_51KyUN7HLyCan9nQSTxGTJhRXTh0wG42ffa3kVF2L3AFRsZkLP950wxN0SsBmCpNSWHIGkRehjILYTmAcOExiFUcR00tHQXlKgM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buyerBind= ActivityBuyerBinding.inflate(layoutInflater)
        setContentView(buyerBind.root)





        val accountInfoApear:TextView=findViewById(R.id.getAccountInfoTextView)
        val confirmBuyerBtn:Button=findViewById(R.id.confirmBuyerBtn)
        val askInfoAgainBtn:Button=findViewById(R.id.askInfoAgainBtn)
        cardLoader=findViewById(R.id.cardLoader)


        val fStore:FirebaseFirestore= FirebaseFirestore.getInstance()
        val auth:FirebaseAuth= FirebaseAuth.getInstance()

        val recieverId:String= intent.getStringExtra("recieverId").toString()

/////////////////Showing Account Info//////////////////////////////////


//////////////////Card Payment///////////////////////////////////
        PaymentConfiguration.init(this,PUBLISH_KEY)
        paymentSheet= PaymentSheet(this) { paymentSheetResult -> onPaymentSheetResult(paymentSheetResult) }


        buyerBind.payWithCard.setOnClickListener {
      cardLoader.visibility=View.VISIBLE
            Handler().postDelayed({
                paymentFlow()
                cardLoader.visibility=View.GONE
            }, 3000)
}




val customerReq:StringRequest= object:StringRequest(Request.Method.POST,
                   "https://api.stripe.com/v1/customers",
    {
val obj= JSONObject(it)
        customerId=obj.getString("id")
        getEphericalKey(customerId)


    }, {

    }
){
    override fun getHeaders(): MutableMap<String, String> {

        val map= hashMapOf<String,String>()
        map.put("Authorization","Bearer "+SECRET_KEY)
        return map
    }
}


val reqQueue:RequestQueue=Volley.newRequestQueue(this)
        reqQueue.add(customerReq)





    }//oncreate

    private fun getEphericalKey(customerId: String) {
        val ephericalKeyReq:StringRequest= object :StringRequest(Request.Method.POST,
            "https://api.stripe.com/v1/ephemeral_keys",
            {
                val obj= JSONObject(it)
               ephericalKey=obj.getString("id")

        getClientSecret(customerId, ephericalKey)

            }, {

            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val map= hashMapOf<String,String>()
                map.put("Authorization","Bearer "+SECRET_KEY)
                map.put("Stripe-Version","2020-08-27")
                return map
            }

            override fun getParams(): MutableMap<String, String> {
                val map= hashMapOf<String,String>()
                map.put("customer",customerId)
                return map
            }
        }


        val reqQueue:RequestQueue=Volley.newRequestQueue(this)
        reqQueue.add(ephericalKeyReq)
    }

    private fun getClientSecret(customerId: String, ephericalKey: String) {
        val clientSecretReq=object :StringRequest(Request.Method.POST,
            "https://api.stripe.com/v1/payment_intents",
            {
                val obj= JSONObject(it)
                clientSecret=obj.getString("client_secret")

            }, {

            }
        ){
            override fun getHeaders(): MutableMap<String, String> {

                val map= hashMapOf<String,String>()
                map.put("Authorization","Bearer "+SECRET_KEY)
                return map
            }

            override fun getParams(): MutableMap<String, String> {
               val map= hashMapOf<String,String>()
                map.put("customer",customerId)
                map.put("amount","1000")
                map.put("currency","usd")
                map.put("automatic_payment_methods[enabled]","true")
                return map
            }
        }
        val reqQueue:RequestQueue=Volley.newRequestQueue(this)
        reqQueue.add(clientSecretReq)
    }

    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(clientSecret,
        PaymentSheet.Configuration("Enzo", PaymentSheet.CustomerConfiguration(customerId,ephericalKey)))
    }



    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        // implemented in the next steps
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            }
        }

    }
}//main


