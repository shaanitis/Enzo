package com.example.enzo

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.enzo.Models.AccountInfo
import com.example.enzo.NotificationWork.DataNotification
import com.example.enzo.NotificationWork.PushNotification
import com.example.enzo.NotificationWork.RetrofitInstance
import com.example.enzo.NotificationWork.SenderNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
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
    lateinit var token:String


    lateinit var sellingAccountId:EditText
    lateinit var sellingAccountPassword:EditText
    lateinit var sellingAccountDetails:EditText
    lateinit var uploadSellingAccountInfo: Button
    var sellerRevNo:Int=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()

        val buyerID:String=intent.getStringExtra("buyerID").toString()
        val sellerID:String=intent.getStringExtra("sellerID").toString()
        val buyerSellerRoom:String= buyerID+sellerID
        val sellerBuyerRoom:String=sellerID+buyerID

        sellingAccountId=findViewById(R.id.addSellingAccountId)
        sellingAccountPassword=findViewById(R.id.addSellingAccountPassword)
        sellingAccountDetails=findViewById(R.id.addSellingAccountDetails)
        uploadSellingAccountInfo=findViewById(R.id.uploadSellingAccountInfo)


        ////checking if its 2nd/3rd/4th revision///////////

        var sp: SharedPreferences = getSharedPreferences("sellerRev", MODE_PRIVATE)
        if (sp.contains("sellerRev")){
            if (sp.getInt("sellerRev", 1)>=1){

               sellerRevNo=sellerRevNo+1

            }
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            token=it.toString()
            Log.d("tkkkkk", token.toString())
        }
        val apiService= RetrofitInstance
        uploadSellingAccountInfo.setOnClickListener {


            var sp: SharedPreferences = getSharedPreferences("sellerRev", MODE_PRIVATE)
            val ed: SharedPreferences.Editor= sp.edit()
            ed.putInt("sellerRev", sellerRevNo)
            ed.commit()



            val accountInfoModel: AccountInfo = AccountInfo( accountID = sellingAccountId.text.toString()
                , accountPassword = sellingAccountPassword.text.toString()
                , accountDetail = sellingAccountDetails.text.toString()
                , revNo = sellerRevNo)

            val sR: DocumentReference = fStore.collection("users")
                .document(buyerID).collection("AccountDetails").document()

            sR.set(accountInfoModel)

    /*        Toast.makeText(this, "Uploaded to $buyerID", Toast.LENGTH_SHORT).show()*/

            /*val rR: DocumentReference = fStore.collection("users")
                .document(buyerID).collection("Account Details").document()

            rR.set(accountInfoModel)*/

//notificationWork

            val title=sellingAccountId.text.toString()
            val message=sellingAccountPassword.text.toString()


            val senderNotification=SenderNotification(token,title,message,this, this)

            PushNotification(DataNotification(title, message), TOPIC ).also {
               sendNotification(it)

            }

        }




    }//oncreate


/////notification work
    private fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {
        try {
            val response=RetrofitInstance.api.postNotification(notification)

            if (response.isSuccessful) {
                Log.d("pyar", "Response: ${Gson().toJson(response)}")

            }else{
                Log.d("etron", response.errorBody().toString())

            }
        } catch (e:Exception){
            Log.d("taggerr", e.toString())

        }
}



}//main