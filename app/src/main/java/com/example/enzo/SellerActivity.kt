package com.example.enzo

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.enzo.Models.AccountInfo
import com.example.enzo.NotificationWork.DataNotification
import com.example.enzo.NotificationWork.PushNotification
import com.example.enzo.NotificationWork.RetrofitInstance
import com.example.enzo.NotificationWork.SenderNotification
import com.facebook.share.Share
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()


        val recieverId:String= intent.getStringExtra("recieverID").toString()
        val recieverName:String=intent.getStringExtra("nameOfOther").toString()


        val senderId:String=auth.currentUser?.uid.toString()
        val senderRoom:String= senderId+recieverId
        val recieverRoom:String=recieverId+senderId

        sellingAccountId=findViewById(R.id.addSellingAccountId)
        sellingAccountPassword=findViewById(R.id.addSellingAccountPassword)
        sellingAccountDetails=findViewById(R.id.addSellingAccountDetails)
        uploadSellingAccountInfo=findViewById(R.id.uploadSellingAccountInfo)
        detailsToBuyerText=findViewById(R.id.detailsToBuyerText)
        detailsToBuyerText.text="Provide details to $recieverName"
        sellerConfirmBtn=findViewById(R.id.confirmSellerBtn)


        ////putting seller rev to shared preferences




        uploadSellingAccountInfo.setOnClickListener {

          var sellerRevNo:Int=1
            fStore.collection("users").document(recieverId).collection("accountDetails").get()
                .addOnSuccessListener(object :OnSuccessListener<QuerySnapshot>{
                    override fun onSuccess(qs: QuerySnapshot?) {

                    }

                })


                    val accountInfoModel: AccountInfo = AccountInfo(
                                     accountID = sellingAccountId.text.toString(),
                                     accountPassword = sellingAccountPassword.text.toString(),
                                     accountDetail = sellingAccountDetails.text.toString(),
                                     revNo = sellerRevNo
                                 )

                                 val sR: DocumentReference = fStore.collection("users")
                                     .document(recieverId).collection("accountDetails").document()

                                 sR.set(accountInfoModel)


            }



//notificationWork








    }//oncreate

}//main