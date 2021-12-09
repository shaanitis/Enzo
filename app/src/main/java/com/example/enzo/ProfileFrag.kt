package com.example.enzo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.enzo.R
import com.facebook.login.LoginManager
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text


class ProfileFrag : Fragment() {
///declaring
   lateinit var auth: FirebaseAuth
   lateinit var fStore: FirebaseFirestore
    lateinit var profilePic: ImageView
    lateinit var profileName: TextView
    lateinit var logoutView: CardView
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
        auth= FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        ////////////getting user id of current user from auth
        val userId= auth.currentUser?.uid.toString()
//////getting detail of current user from firestore
        val documentReference: DocumentReference= fStore.collection("users").document(userId)
        documentReference.get().addOnSuccessListener {
            profileName.text= it.getString("profileName")
            val picUrl:String= it.getString("profileUrl").toString()
           Glide.with(requireContext()).load(picUrl).placeholder(R.drawable.ic_person).into(profilePic)
        }
///////////logging out
        logoutView.setOnClickListener {
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut()
            val intent=Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }










        return view
    }//on craete


}//main