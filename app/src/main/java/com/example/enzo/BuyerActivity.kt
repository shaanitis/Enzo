package com.example.enzo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.enzo.Models.MessageModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class BuyerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer)


        val accountInfoApear:TextView=findViewById(R.id.accountInfoAppear)
        val addPaymentAmount:EditText=findViewById(R.id.addPaymentAmount)
        val paymentDoneBtn:Button=findViewById(R.id.paymentDoneBtn)
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

        val sellerID=intent.getStringExtra("sellerID")
        val buyerID=intent.getStringExtra("buyerID")
         val buyerSellerRoom:String= buyerID+sellerID

        paymentDoneBtn.setOnClickListener {

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

        }


    }//oncreate
}//main