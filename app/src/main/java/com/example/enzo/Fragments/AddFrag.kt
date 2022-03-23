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
import com.example.enzo.Models.AdModel
import com.example.enzo.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
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

    lateinit var category: String
    lateinit var userId: String

    var imageName = UUID.randomUUID().toString() + ".jpg"

    lateinit var pD: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        val view: android.view.View = inflater.inflate(R.layout.fragment_add, container, false)
///initializing
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()
        db = FirebaseDatabase.getInstance().getReference()
        adTitle = view.findViewById(R.id.adTitle)
        adDetail = view.findViewById(R.id.adDetail)
        adPrice = view.findViewById(R.id.adPrice)
        adTitleImg = view.findViewById(R.id.adTitleImg)
        nextBtn = view.findViewById(R.id.nextBtn)
        pD = ProgressDialog(requireContext())
        userId = auth.currentUser?.uid.toString()


///getting category from previous category frag


        try {
            val arg = this.arguments
            category = arg?.get("category").toString()

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
            } else {
                adDetail.clearFocus()
                adPrice.clearFocus()
                adTitle.clearFocus()

                pD.show()
                hideKeyboard(requireActivity())

                var adNo = UUID.randomUUID().toString()

                storageReference =
                    FirebaseStorage.getInstance().getReference("images/" + imageName)

                storageReference
                    .putFile(imageUri).addOnSuccessListener {
//////getting download url of image just uploaded
                        storageReference?.downloadUrl?.addOnSuccessListener(
                            OnSuccessListener<Any?> { downloadUrl ->
                                pD.hide()
                                adDetail.clearFocus()
                                adPrice.clearFocus()
                                adTitle.clearFocus()
                                val adTitleImgUrl = downloadUrl.toString()

                                findNavController().navigate(
                                    R.id.action_addFrag_to_uploadFrag,
                                    Bundle().apply {
                                        putString("adTitle", adTitle.text.toString())
                                        putString("adPrice", adPrice.text.toString())
                                        putString("adDetail", adDetail.text.toString())
                                        putString("adTitleImgUrl", adTitleImgUrl)
                                        putString("adCategory", category)
                                        putString("imgsUrl", adNo)
                                    })
                            })
                    }
            }
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
    }////////////////////////////////////////onnnnnnnnnnnnn createeeeeeeeeeeeeee//////////////////////////////////////////////


/////onActivity result of opening gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    if (requestCode == CODE_ONE && data != null) {


        imageUri = data.data!!


    }



}
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }
    private fun checkTextViews(){

    }

}


