package com.example.enzo.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import com.example.enzo.Models.AdModel
import com.example.enzo.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class AddFrag : Fragment() {

////declaring
     lateinit var auth:FirebaseAuth
     lateinit var storageReference: StorageReference
    lateinit var checkText:TextView
    lateinit var db:FirebaseFirestore
    lateinit var imageUri: Uri
    lateinit var noOfImages: TextView
    lateinit var testImage:ImageView
    lateinit var adTitle: EditText
    lateinit var adDetail:EditText
    lateinit var adPrice: EditText
    lateinit var adImage:Button
    lateinit var adUploadBtn:Button
    lateinit var radioGroup:RadioGroup
   lateinit var radioButton:RadioButton
    var progressDialogue: ProgressDialog?= null
    var imageName= UUID.randomUUID().toString() + ".jpg"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        val view: android.view.View = inflater.inflate(R.layout.fragment_add, container, false)
///initializing
       auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
        storageReference=FirebaseStorage.getInstance().getReference()
        adTitle=view.findViewById(R.id.adTitle)
        adDetail=view.findViewById(R.id.adDetail)
        adPrice=view.findViewById(R.id.adPrice)
        adImage=view.findViewById(R.id.adImage)
        noOfImages=view.findViewById(R.id.noOfImages)
        testImage=view.findViewById(R.id.testImage)
        adUploadBtn=view.findViewById(R.id.adUploadBtn)
        radioGroup=view.findViewById(R.id.radioGroup)


          var adType:String="null"

//////opening gallery on button click///////
        adImage.setOnClickListener {
            val intent:Intent= Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 100)

        }
///uploading image and ad
        adUploadBtn.setOnClickListener {
            progressDialogue= ProgressDialog(requireContext())
            progressDialogue!!.setTitle("Uploading your ad....")
            progressDialogue!!.show()
            progressDialogue!!.setCancelable(false);

//////storing the id of option clicked in radioId then getting radiobutton clicked by putting that id in radio button
            val radioId: Int = radioGroup.checkedRadioButtonId
            radioButton = view.findViewById(radioId)
            val userId= auth.currentUser?.uid.toString()



////uploading image to firebase storage
            storageReference= FirebaseStorage.getInstance().getReference("images/"+ imageName)
            storageReference.putFile(imageUri).addOnSuccessListener {
//////getting download url of image just uploaded
                storageReference?.downloadUrl?.addOnSuccessListener(OnSuccessListener<Any?> { downloadUrl ->

                    val url = downloadUrl.toString()

/////making object of data class model 'AdMode' and uploading details that we got from user///////
                    val adModel= AdModel(adTitle = adTitle.text.toString()
                        , adDetail = adDetail.text.toString()
                        , adPrice = adPrice.text.toString()
                        , adImageUrl =url
                        , adType= radioButton.text.toString()
                    , adUserId = userId
                    , adSearchTitle = adTitle.text.toString().toLowerCase())
//////uploading to firestore
                    val dR: DocumentReference = db.collection("ads")
                        .document()

                    dR.set(adModel)
                    Toast.makeText(requireContext(), "Ad Uploaded", Toast.LENGTH_SHORT).show()
                    if(progressDialogue!!.isShowing) {
                        progressDialogue!!.hide()
                    }

                    adTitle.text=null
                    adDetail.text=null
                    adPrice.text=null



                })?.addOnFailureListener(OnFailureListener {
                    Toast.makeText(requireContext(), "Failed to get Url", Toast.LENGTH_SHORT).show()
                    if(progressDialogue!!.isShowing) {
                        progressDialogue!!.hide()
                    }
                })
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to upload", Toast.LENGTH_SHORT).show()
                if(progressDialogue!!.isShowing) {
                    progressDialogue!!.hide()
                }
            }


        }


        return view
    }
/////onActivity result of opening gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==100 && data!=null && data.data!=null){
            imageUri= data.data!!


        }
    }

}
