package com.example.enzo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.Preference
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.example.enzo.Fragments.AddFrag
import com.example.enzo.Models.AccountInfo
import com.example.enzo.NotificationWork.SenderNotification
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val TOPIC="/topics/myTopic"
class SellerActivity : AppCompatActivity() {

/////declaring
    lateinit var auth:FirebaseAuth
    lateinit var fStore:FirebaseFirestore

    lateinit var sellingAccountId:EditText
    lateinit var sellingAccountPassword:EditText
    lateinit var sellingAccountDetails:EditText
    lateinit var uploadSellingAccountInfo: MaterialButton
    lateinit var detailsToBuyerText:TextView
    lateinit var sellerConfirmBtn:Button
    lateinit var revSP:SharedPreferences
    var nameOfBuyer:String?=null
    var token:String?=null

    var check4:Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()

         revSP=getSharedPreferences("revSP", MODE_PRIVATE)
        val revSPEditor=revSP.edit()

        var revNo:Int=1
        val recieverId:String= intent.getStringExtra("recieverId").toString()
        val recieverName:String=intent.getStringExtra("nameOfOther").toString()


        sellingAccountId=findViewById(R.id.addSellingAccountId)
        sellingAccountPassword=findViewById(R.id.addSellingAccountPassword)
        sellingAccountDetails=findViewById(R.id.addSellingAccountDetails)
        uploadSellingAccountInfo=findViewById(R.id.uploadSellingAccountInfo)
        detailsToBuyerText=findViewById(R.id.detailsToBuyerText)
        detailsToBuyerText.text="Provide details to $recieverName"

        sellerConfirmBtn=findViewById(R.id.confirmSellerBtn)


        ////putting seller rev to shared preferences



        uploadSellingAccountInfo.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {
                fStore.collection("users").document(recieverId)
                    .collection("accountDetails").whereEqualTo("revNo", 4)
                    .get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                        override fun onSuccess(p0: QuerySnapshot?) {
                            if (p0!!.isEmpty) {
                        check4==true
                            }else{
                                Toast.makeText(
                                    this@SellerActivity,
                                    "Limit Reached, you should close the deal.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
            }
if (check4) {
    if (sellingAccountId.text.toString().isEmpty()) {
        sellingAccountId.requestFocus()
        sellingAccountId.setError("Provide Id/Gmail to Buyer")
    } else if (sellingAccountPassword.text.toString().isEmpty()) {
        sellingAccountPassword.requestFocus()
        sellingAccountPassword.setError("Provide password of account")
    } else {

        lifecycleScope.launch(Dispatchers.IO) {
            fStore.collection("users").document(recieverId)
                .collection("accountDetails").whereEqualTo("revNo", 1)
                .get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(p0: QuerySnapshot?) {
                        if (p0!!.isEmpty) {
                        } else {
                            revNo = 2
                        }
                    }

                })
            fStore.collection("users").document(recieverId)
                .collection("accountDetails").whereEqualTo("revNo", 2)
                .get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(p0: QuerySnapshot?) {
                        if (p0!!.isEmpty) {
                        } else {
                            revNo = 3
                        }
                    }

                })
            fStore.collection("users").document(recieverId)
                .collection("accountDetails").whereEqualTo("revNo", 3)
                .get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                    override fun onSuccess(p0: QuerySnapshot?) {
                        if (p0!!.isEmpty) {
                        } else {
                            revNo = 4
                        }
                    }

                })
        }

        sellingAccountId.clearFocus()
        sellingAccountPassword.clearFocus()
        MainActivity.HideKeyboard.hideKeyboard(this)


        val accountInfo = AccountInfo(
            sellingAccountId.text.toString(),
            sellingAccountPassword.text.toString(),
            sellingAccountDetails.text.toString(), revNo
        )

        fStore.collection("users").document(recieverId)
            .collection("accountDetails").document()
            .set(accountInfo)
            .addOnSuccessListener {
                val idPassString: String ="Sent account ID & Password."
                sellingAccountPassword.text.toString()
                fStore.collection("users").document(recieverId).get()
                    .addOnSuccessListener {
                        nameOfBuyer = it.getString("profileName").toString()
                        token = it.getString("token").toString()

                    }

                fStore.collection("users").document(auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        val nameOfSeller = it.getString("profileName").toString()
                        val send: SenderNotification = SenderNotification(
                            token!!, nameOfSeller,
                            idPassString, applicationContext, this
                        )
                        send.SendNotifications()
                    }
            }

    }

}



                /*  val checkRevNo=revSP.getInt("revNo",0)
            if (checkRevNo>4){
                Toast.makeText(this, "Limit Reached, you should close the deal.", Toast.LENGTH_SHORT).show()
            }else if (checkRevNo>0){
                revNo=checkRevNo+1
            }

            Toast.makeText(this, "$revNo", Toast.LENGTH_SHORT).show()
            if (sellingAccountId.text.toString().isEmpty()) {
                sellingAccountId.requestFocus()
                sellingAccountId.setError("Provide Id/Gmail to Buyer")
            } else if (sellingAccountPassword.text.toString().isEmpty()) {
                sellingAccountPassword.requestFocus()
                sellingAccountPassword.setError("Provide password of account")
            } else {



                sellingAccountId.clearFocus()
                sellingAccountPassword.clearFocus()
                MainActivity.HideKeyboard.hideKeyboard(this)



                fStore.collection("users").document(recieverId)
                    .collection("accountDetails").document()
                    .set(
                        AccountInfo(
                            sellingAccountId.text.toString(),
                            sellingAccountPassword.text.toString(),
                            sellingAccountDetails.text.toString(), revNo
                        )
                    ).addOnSuccessListener {
                        revSPEditor.apply {
                            putInt("revNo",revNo)
                            apply()

                        }
                    }

            }*/


//notificationWork


        }





    }//oncreate


}//main