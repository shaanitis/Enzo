package com.example.enzo.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.enzo.LoginActivity
import com.example.enzo.R
import com.facebook.login.LoginManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class ProfileFrag : Fragment() {
///declaring
   lateinit var auth: FirebaseAuth
   lateinit var fStore: FirebaseFirestore
    lateinit var profilePic: ImageView
    lateinit var profileName: TextView
    lateinit var logoutView: TextView
    lateinit var myAdsBtn:TextView
    lateinit var myList: ArrayList<String>
    lateinit var userID:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        val view: View= inflater.inflate(R.layout.fragment_profile, container, false)
/////initializng
      profilePic = view.findViewById(R.id.profilePic)
       profileName= view.findViewById(R.id.profileName)
        logoutView= view.findViewById(R.id.logoutView)
        myAdsBtn=view.findViewById(R.id.myAdsBtn)
        userID= "0"
        myList= arrayListOf()
        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        ////////////getting user id of current user from auth

//////getting detail of current user from firestore
        val documentReference: DocumentReference= fStore.collection("users")
            .document(auth.currentUser?.uid.toString())
        documentReference.get().addOnSuccessListener {
          try {
              profileName.text= it.getString("profileName")
              val picUrl:String= it.getString("profileUrl").toString()
              Glide.with(requireContext()).load(picUrl).placeholder(R.drawable.ic_person).into(profilePic)
          }  catch (e:Exception){
              Log.e("error", e.message.toString())
          }
        }
///////////logging out
        logoutView.setOnClickListener {
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut()
            val intent=Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        myAdsBtn.setOnClickListener {

            val extras:FragmentNavigator.Extras= FragmentNavigator.Extras.Builder()
                .addSharedElement(myAdsBtn, "myAds")
                .build()
            findNavController().navigate(R.id.action_profileFrag_to_myAdsFrag
            , null
            ,null
            ,extras)

        }




        val callback=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_profileFrag_to_homeFrag)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)








        return view
    }//on craete


}//main