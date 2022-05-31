package com.example.enzo.Fragments

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.enzo.LoginActivity
import com.example.enzo.R
import com.example.enzo.TryActivity
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception


class ProfileFrag : Fragment() {
///declaring
   lateinit var auth: FirebaseAuth
   lateinit var fStore: FirebaseFirestore
    lateinit var profilePic: ImageView
    lateinit var profileName: TextView
    lateinit var logoutView: TextView
    lateinit var myAdsBtn:TextView
    lateinit var tryBtn:TextView
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
        tryBtn=view.findViewById(R.id.tryBtn)
        userID= "0"
        myList= arrayListOf()
        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        ////////////getting user id of current user from auth

//////getting detail of current user from firestore

        showUserProfile()


///////////logging out
        logoutView.setOnClickListener {

           try {

               Firebase.auth.signOut()
               LoginManager.getInstance().logOut()
               val intent = Intent(requireContext(), LoginActivity::class.java)
               startActivity(intent)
           }catch (e:Exception){
               Log.d("","")
           }
        }
        tryBtn.setOnClickListener {
            Intent(requireContext(), TryActivity::class.java).also {
                startActivity(it)  }
        }
        myAdsBtn.setOnClickListener {

          try {


              val extras: FragmentNavigator.Extras = FragmentNavigator.Extras.Builder()
                  .addSharedElement(myAdsBtn, "myAds")
                  .build()
              findNavController().navigate(
                  R.id.action_profileFrag_to_myAdsFrag, null, null, extras
              )
          }catch (e:Exception){
              Log.d("","")
          }
        }


try {


    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigate(R.id.action_profileFrag_to_homeFrag)
        }

    }
    requireActivity().onBackPressedDispatcher.addCallback(callback)

}catch (e:Exception){
    Log.d("","")
}






        return view
    }//on craete

    private fun showUserProfile() {

        try {
       lifecycleScope.async(Dispatchers.IO) {

           val job=async {
            val documentReference: DocumentReference = fStore.collection("users")
                .document(auth.currentUser?.uid.toString())
            documentReference.get().addOnSuccessListener {


                profileName.text = it.getString("profileName")
                val picUrl: String = it.getString("profileUrl").toString()
                Picasso.get().load(picUrl).placeholder(R.drawable.ic_person)
                    .into(profilePic)
                ///anim
                try {


                val cx: Int = profilePic.getMeasuredWidth() / 2
                val cy: Int = profilePic.getMeasuredHeight() / 2

                // get the final radius for the clipping circle

                // get the final radius for the clipping circle
                val finalRadius: Int = Math.max(profilePic.getWidth(), profilePic.getHeight()) / 2

                // create the animator for this view (the start radius is zero)

                // create the animator for this view (the start radius is zero)
                val anim: Animator =
                    ViewAnimationUtils.createCircularReveal(profilePic, cx, cy, 0f, finalRadius.toFloat())

                // make the view visible and start the animation
                anim.start()
                // make the view visible and start the animation
            }catch (e:Exception){
               Log.d("err","hey")
           }
                profilePic.setVisibility(View.VISIBLE)


            }
            }
        }
        } catch (e: Exception) {
            Log.e("error", e.message.toString())
        }
    }


}//main