package com.example.enzo.Fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.example.enzo.MainActivity
import com.example.enzo.Models.AdModel
import com.example.enzo.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class AddFrag : Fragment() {

    ////declaring
    private final var CODE_ALL = 100
    private final var CODE_ONE = 50
    lateinit var auth: FirebaseAuth
    lateinit var storageReference: StorageReference

    lateinit var checkText: TextView
    lateinit var fStore: FirebaseFirestore
    lateinit var db: DatabaseReference
    lateinit var imageUri: Uri
    lateinit var adTitle: EditText
    lateinit var adDetail: EditText
    lateinit var adPrice: EditText
    lateinit var adTitleImg: CardView
    lateinit var nextBtn: Button
    lateinit var adTitleImgText:TextView
 lateinit var uploadedImgView:ImageView
    lateinit var category: String
    lateinit var userId: String
    lateinit var goBackBtn:ImageButton
    lateinit var adBid:EditText

    var imageName = UUID.randomUUID().toString() + ".jpg"

    lateinit var pD: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        val view: android.view.View = inflater.inflate(R.layout.fragment_add, container, false)
///initializing
        val navBar:BottomNavigationView= requireActivity().findViewById(R.id.navBar)
        navBar.visibility=View.GONE
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()
        db = FirebaseDatabase.getInstance().getReference()
        uploadedImgView=view.findViewById(R.id.uploadedImgView)
        adTitleImgText=view.findViewById(R.id.addTitleImgText)
        adTitle = view.findViewById(R.id.adTitle)
        adDetail = view.findViewById(R.id.adDetail)
        adPrice = view.findViewById(R.id.adPrice)
        adTitleImg = view.findViewById(R.id.adTitleImg)
        nextBtn = view.findViewById(R.id.nextBtn)
        goBackBtn=view.findViewById(R.id.goBackBtnAdd)
        adBid=view.findViewById(R.id.adBid)
        pD = ProgressDialog(requireContext())
        userId = auth.currentUser?.uid.toString()


///getting category from previous category frag


        try {
            /*val arg = this.arguments*/
            category = requireArguments().getString("adCategory").toString()

        } catch (e: Exception) {
            Log.e("", "")
        }
        pD.setTitle("Please Wait")
        pD.setMessage("Adding your Image and Info")
        pD.setCancelable(false)


//////opening gallery to select title image//////
        adTitleImg.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    CODE_ONE
                )

            } else {

                val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CODE_ONE)

            }
        }

///uploading image and ad
        nextBtn.setOnClickListener {
            if (adDetail.text.toString().isEmpty()) {
                adDetail.requestFocus()
                adDetail.setError("Add Detail")
            } else if (adTitle.text.toString().isEmpty()) {
                adTitle.requestFocus()
                adTitle.setError("Add Title")
            } else if (adPrice.text.toString().isEmpty()) {
                adPrice.requestFocus()
                adPrice.setError("Add Price")
            }
            else {
                adDetail.clearFocus()
                adPrice.clearFocus()
                adTitle.clearFocus()
                adBid.clearFocus()


                MainActivity.HideKeyboard.hideKeyboard(requireActivity())
                pD.show()


                var adNo = UUID.randomUUID().toString()

                storageReference =
                    FirebaseStorage.getInstance().getReference("images/" + imageName)

                storageReference
                    .putFile(imageUri!!).addOnSuccessListener {
//////getting download url of image just uploaded
                        storageReference?.downloadUrl?.addOnSuccessListener(
                            OnSuccessListener<Any?> { downloadUrl ->
                                pD.hide()
                                adDetail.clearFocus()
                                adPrice.clearFocus()
                                adTitle.clearFocus()
                                adBid.clearFocus()
                                val adTitleImgUrl = downloadUrl.toString()

                                findNavController().navigate(
                                    R.id.action_addFrag_to_uploadFrag,
                                    Bundle().apply {
                                        putString("adTitle", adTitle.text.toString())
                                        putString("adPrice", adPrice.text.toString())
                                        putString("adBid", adBid.text.toString())
                                        putString("adDetail", adDetail.text.toString())
                                        putString("adTitleImgUrl", adTitleImgUrl)
                                        putString("adCategory", category)
                                        putString("imgsUrl", adNo)
                                    })
                            })
                    }
            }
        }

///onBackBtn
        goBackBtn.setOnClickListener {

                    findNavController().navigate(R.id.action_addFrag_to_adCategory)


        }

////navigating back to adCategoryFrag on onBackPress
        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_addFrag_to_adCategory)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
////uploading image to firebase storage



        return view

    }/////on create/////////////


/////onActivity result of opening gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    if (requestCode == CODE_ONE && data != null) {


        imageUri = data.data!!
        uploadedImgView.setImageURI(imageUri)
        adTitleImgText.text="Added Title Image."


    }



}

}


