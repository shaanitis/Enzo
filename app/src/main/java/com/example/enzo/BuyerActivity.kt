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




class BuyerActivity : AppCompatActivity() {

    lateinit var buyerBind:ActivityBuyerBinding
    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String
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
        val paymentDoneBtn:Button=findViewById(R.id.payWithEasypaisaBtn)
        val confirmBuyerBtn:Button=findViewById(R.id.confirmBuyerBtn)
        val askInfoAgainBtn:Button=findViewById(R.id.askInfoAgainBtn)
        ///rev btns
        val revBtnsLayout:LinearLayout=findViewById(R.id.revBtnsLayout)
        val rev1Btn:Button=findViewById(R.id.rev1Btn)
        val rev2Btn:Button=findViewById(R.id.rev2Btn)
        val rev3Btn:Button=findViewById(R.id.rev3Btn)
        val rev4Btn:Button=findViewById(R.id.rev4Btn)

        val fStore:FirebaseFirestore= FirebaseFirestore.getInstance()
        val auth:FirebaseAuth= FirebaseAuth.getInstance()

        val recieverId:String= intent.getStringExtra("recieverID").toString()

        val sellerID=intent.getStringExtra("sellerID")
        val buyerID=intent.getStringExtra("buyerID")
         val buyerSellerRoom:String= buyerID+sellerID

        PaymentConfiguration.init(this,PUBLISH_KEY)
        paymentSheet= PaymentSheet(this) { paymentSheetResult -> onPaymentSheetResult(paymentSheetResult) }


        buyerBind.payWithEasypaisaBtn.setOnClickListener {
            val launchIntent=packageManager.getLaunchIntentForPackage("com.facebook.lite")
            if (launchIntent!=null) {
                startActivity(launchIntent)
            }
        }
        buyerBind.payWithCard.setOnClickListener {
            paymentFlow()
}
 /*       paymentDoneBtn.setOnClickListener {

            Toast.makeText(this, "Your payment is under processing and safe until all 4 revisions are complete", Toast.LENGTH_SHORT).show()
            fStore.collection("users").document(auth.currentUser!!.uid).collection("AccountDetails")
                .get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(qs: QuerySnapshot?) {

                        for (qds: QueryDocumentSnapshot in qs!!) {
                            val accountID: String=qds.getString ("accountID").toString()
                            val accountPassword: String=qds.getString("accountPassword").toString()
                            val accountDetails: String= qds.getString("accountDetail").toString()
                            val revNo: Int= qds.getDouble("revNo")!!.toInt()

                            accountInfoApear.text="ID: $accountID \nPassword: $accountPassword \nDetails: $accountDetails \nRevision No: $revNo"
                        }


                    }
                })
            revBtnsLayout.visibility=View.VISIBLE

        }

        askInfoAgainBtn.setOnClickListener {
           Toast.makeText(this, "We have asked the seller to provide details again. Kindly wait for response", Toast.LENGTH_LONG).show()

        }
        confirmBuyerBtn.setOnClickListener {
            Toast.makeText(this, "Deal confirmed! Payment transferred to seller.", Toast.LENGTH_LONG).show()

        }

        ///rev Btns
        rev1Btn.setOnClickListener {
            fStore.collection("users").document(auth.currentUser!!.uid).collection("AccountDetails")
                .whereEqualTo("revNo", 1)
                .get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(querySnapshot: QuerySnapshot?) {
                        for (qds: QueryDocumentSnapshot in querySnapshot!!){
                            val accountID: String=qds.getString ("accountID").toString()
                            val accountPassword: String=qds.getString("accountPassword").toString()
                            val accountDetails: String= qds.getString("accountDetail").toString()
                            val revNo: Int= qds.getDouble("revNo")!!.toInt()

                            if (revNo==1) {
                                accountInfoApear.text =
                                    "ID: $accountID \nPassword: $accountPassword \nDetails: $accountDetails \nRevision No: $revNo"
                            }else accountInfoApear.text=="Not Provided Yet"
                        }
                    }

                })

        }
        rev2Btn.setOnClickListener {
            fStore.collection("users").document(auth.currentUser!!.uid).collection("AccountDetails")
                .whereEqualTo("revNo", 2)
                .get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(querySnapshot: QuerySnapshot?) {
                        for (qds: QueryDocumentSnapshot in querySnapshot!!){
                            val accountID: String=qds.getString ("accountID").toString()
                            val accountPassword: String=qds.getString("accountPassword").toString()
                            val accountDetails: String= qds.getString("accountDetail").toString()
                            val revNo: Int= qds.getDouble("revNo")!!.toInt()

                              if (revNo==2) {
                                  accountInfoApear.text =
                                      "ID: $accountID \nPassword: $accountPassword \nDetails: $accountDetails \nRevision No: $revNo"
                              } else accountInfoApear.text="Not provided Yet"
                        }
                    }

                })

        }
        rev3Btn.setOnClickListener {
            fStore.collection("users").document(auth.currentUser!!.uid).collection("AccountDetails")
                .whereEqualTo("revNo", 3)
                .get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(querySnapshot: QuerySnapshot?) {
                        for (qds: QueryDocumentSnapshot in querySnapshot!!){
                            val accountID: String=qds.getString ("accountID").toString()
                            val accountPassword: String=qds.getString("accountPassword").toString()
                            val accountDetails: String= qds.getString("accountDetail").toString()
                            val revNo: Int= qds.getDouble("revNo")!!.toInt()

                            if (revNo==3) {
                                accountInfoApear.text =
                                    "ID: $accountID \nPassword: $accountPassword \nDetails: $accountDetails \nRevision No: $revNo"
                            }else accountInfoApear.text=="Not Provided Yet"
                        }
                    }

                })


        }
        rev4Btn.setOnClickListener {
            fStore.collection("users").document(auth.currentUser!!.uid).collection("AccountDetails")
                .whereEqualTo("revNo", 3)
                .get()
                .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(querySnapshot: QuerySnapshot?) {
                        for (qds: QueryDocumentSnapshot in querySnapshot!!){
                            val accountID: String=qds.getString ("accountID").toString()
                            val accountPassword: String=qds.getString("accountPassword").toString()
                            val accountDetails: String= qds.getString("accountDetail").toString()
                            val revNo: Int= qds.getDouble("revNo")!!.toInt()

                            if (revNo==4) {
                                accountInfoApear.text =
                                    "ID: $accountID \nPassword: $accountPassword \nDetails: $accountDetails \nRevision No: $revNo"
                            }else accountInfoApear.text=="Not Provided Yet"
                        }
                    }

                })

        }*/



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


