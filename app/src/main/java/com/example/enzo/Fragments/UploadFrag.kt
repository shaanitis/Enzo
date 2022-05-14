package com.example.enzo.Fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.navigation.fragment.findNavController
import com.example.enzo.Models.AdModel
import com.example.enzo.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    lateinit var adLocation:CardView
    lateinit var adUploadBtn: Button
    lateinit var adPhoneNo:EditText
    lateinit var adDetailImages: CardView
    lateinit var noOfImages: TextView
    lateinit var uploadedImg:ImageView
    lateinit var imageList:ArrayList<Uri>
    lateinit var auth: FirebaseAuth
    lateinit var storageReference: StorageReference
    lateinit var fStore: FirebaseFirestore
    lateinit var pD:ProgressDialog
    lateinit var fusedLocation:FusedLocationProviderClient
    var adLocLatitude:String=""
    var adLocLongitude:String=""




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_upload, container, false)
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.navBar)
        navBar.visibility=View.GONE
        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        storageReference= FirebaseStorage.getInstance().getReference()
        adLocation=view.findViewById(R.id.adLocation)
        uploadedImg=view.findViewById(R.id.uploadedImgView)
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
fusedLocation=LocationServices.getFusedLocationProviderClient(requireContext())
adLocation.setOnClickListener {
    checkPermission()

}


        val adTitle= requireArguments().getString("adTitle")
        val adPrice= requireArguments().getString("adPrice")
        val adDetail= requireArguments().getString("adDetail")
        val adTitleImgUrl= requireArguments().getString("adTitleImgUrl")
        val adCategory= requireArguments().getString("adCategory")
        var adNo= requireArguments().getString("imgsUrl")
        adUploadBtn.setOnClickListener {
            if (adLocLatitude==null && adLocLongitude==null) {
                Toast.makeText(requireContext(), "Add Location First", Toast.LENGTH_SHORT).show()
            } else if (adPhoneNo.text.toString().isEmpty()) {
                adPhoneNo.requestFocus()
                adPhoneNo.setError("Add Phone No")
            } else if (imageList.isEmpty()) {
                Toast.makeText(requireContext(), "Provide additional Images", Toast.LENGTH_SHORT).show()
            } else {

                hideKeyboard(requireActivity())
                pD.show()
                for (i in 0 until imageList.size) {
                    var imgNAME = UUID.randomUUID().toString()

                    var uriImg: Uri = imageList.get(i)
                    var imgFolder: StorageReference =
                        FirebaseStorage.getInstance().getReference()
                            .child(requireArguments().getString("imgsUrl").toString())

                    var imgPath = imgFolder.child("image" + uriImg.lastPathSegment)

                    imgPath.putFile(uriImg).addOnSuccessListener {
//////getting download url of image just uploaded
                        imgPath.downloadUrl.addOnSuccessListener(
                            OnSuccessListener<Any?> { downloadUrl ->

                                val url = downloadUrl.toString()


                                val data = hashMapOf(i.toString() to url)
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
                    adLocLatitude,
                    adLocLongitude,
                null)
//////uploading to firestore
                val dR: DocumentReference = fStore.collection("ads")
                    .document()

                dR.set(adModel)

            }
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

    private fun checkPermission() {
        val task=fusedLocation.lastLocation
        if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
      task.addOnSuccessListener {
          if (it!=null){
              adLocLatitude= it.latitude.toString()
              adLocLongitude= it.longitude.toString()
              Toast.makeText(requireContext(), "${it.latitude}, ${it.longitude}", Toast.LENGTH_SHORT).show()
          }
      }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

         if (requestCode == 100 && resultCode== Activity.RESULT_OK && data!=null) {



            var clip: ClipData? = data!!.getClipData()
            if (clip !=null){


                val countImages: Int = clip.itemCount


                for (i in 0 until countImages) {

                   var imageUri: Uri = clip.getItemAt(i).uri
                    imageList.add(imageUri)
                    uploadedImg.setImageURI(imageUri)




            }
                noOfImages.text = " ${imageList.size} images attached."


            }
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
}