package com.example.enzo.Fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.enzo.Models.AdModel
import com.example.enzo.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UploadFrag : Fragment() {

    lateinit var adLocation:EditText
    lateinit var adUploadBtn: Button
    lateinit var adPhoneNo:EditText
    lateinit var adDetailImages: CardView
    lateinit var noOfImages: TextView
    lateinit var imageList:ArrayList<Uri>
    lateinit var auth: FirebaseAuth
    lateinit var storageReference: StorageReference
    lateinit var fStore: FirebaseFirestore
    lateinit var pD:ProgressDialog




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_upload, container, false)

        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        storageReference= FirebaseStorage.getInstance().getReference()
        adLocation=view.findViewById(R.id.adLocation)
        noOfImages=view.findViewById(R.id.noOfImages)
        adUploadBtn=view.findViewById(R.id.adUploadBtn)
        adPhoneNo=view.findViewById(R.id.adPhoneNo)
        adDetailImages=view.findViewById(R.id.adDetailImgs)
        imageList= arrayListOf()
        pD= ProgressDialog(requireContext())



        val userId = auth.currentUser?.uid


        pD.setTitle("Please Wait")
        pD.setMessage("Uploading your Ad!")
        pD.setCancelable(false)


//////opening gallery to select detail images///////
        adDetailImages.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    100
                )

            } else {

                val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(Intent.createChooser(intent, "Select Images"), 100)

            }}


        val adTitle= requireArguments().getString("adTitle")
        val adPrice= requireArguments().getString("adPrice")
        val adDetail= requireArguments().getString("adDetail")
        val adTitleImgUrl= requireArguments().getString("adTitleImgUrl")
        val adCategory= requireArguments().getString("adCategory")
        var adNo= requireArguments().getString("imgsUrl")
        adUploadBtn.setOnClickListener {


            pD.show()
            for (i in 0 until imageList.size) {
                var imgNAME= UUID.randomUUID().toString()

                var uriImg: Uri= imageList.get(i)
                var imgFolder: StorageReference =
                    FirebaseStorage.getInstance().getReference()
                        .child(requireArguments().getString("imgsUrl").toString())

                       var imgPath= imgFolder .child("image"+ uriImg.lastPathSegment)

                imgPath.putFile(uriImg).addOnSuccessListener {
//////getting download url of image just uploaded
                       imgPath.downloadUrl.addOnSuccessListener(
                            OnSuccessListener<Any?> { downloadUrl ->

                                val url = downloadUrl.toString()



                                val data= hashMapOf(i.toString() to url)
                             fStore.collection("adAllImages")
                                    .document(requireArguments().getString("imgsUrl").toString())
                                    .set(data, SetOptions.merge()).addOnSuccessListener {
                                     pD.hide()
                                 }

                            })

                    }
            }



        val adModel = AdModel(
                adTitle,
                adDetail,
                adPrice,
                 adTitleImgUrl,
                adCategory,
                userId,
                adTitle?.toLowerCase(),
                adAllImages = adNo.toString(),
                adPhoneNo.text.toString(),
                adLocation.text.toString()
            )
//////uploading to firestore
            val dR: DocumentReference = fStore.collection("ads")
                .document()

            dR.set(adModel)

        }



////navigating back to addFragon onBackPress
        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_uploadFrag_to_addFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

         if (requestCode == 100 && resultCode== Activity.RESULT_OK && data!=null) {



            var clip: ClipData? = data!!.getClipData()
            if (clip !=null){


                val countImages: Int = clip.itemCount


                for (i in 0 until countImages) {

                   var imageUri: Uri = clip.getItemAt(i).uri
                    imageList.add(imageUri)



            }
                noOfImages.text = "You have selected ${imageList.size} images"

            }
         }

}
}