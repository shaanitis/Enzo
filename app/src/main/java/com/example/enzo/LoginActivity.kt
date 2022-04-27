package com.example.enzo

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.enzo.Models.LoginModel
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

public class LoginActivity : AppCompatActivity() {

///declaring
    lateinit var googleLoginBtn: ImageView
    lateinit var customFbBtn: ImageView
    lateinit var fbLoginBtn:LoginButton
    lateinit var auth: FirebaseAuth
    lateinit var fStore :FirebaseFirestore
    lateinit var progressBar: ProgressBar
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var cbManager: CallbackManager
    var token: String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


/////hiding status bar
        this.supportActionBar?.hide()
/////initialzing
        googleLoginBtn= findViewById(R.id.googleLoginBtn)
        customFbBtn=findViewById(R.id.customfbBtn)
        fbLoginBtn=findViewById(R.id.fbLoginBtn)
        auth = FirebaseAuth.getInstance()
        fStore= FirebaseFirestore.getInstance()
        FacebookSdk.sdkInitialize(applicationContext)
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility=View.GONE

////////Google gso/////////
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

/////////facebook call back manager.///////////
        cbManager = CallbackManager.Factory.create()
        fbLoginBtn.registerCallback(cbManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
                progressBar.visibility=View.VISIBLE
//////////to get user data through graph api/////////////////
                var graphRequest = GraphRequest.newMeRequest(result?.accessToken) { obj, response ->
                    Log.d("facebookData", obj!!.getString("name"))
                    Log.d("facebookData", obj.getString("email"))
                    Log.d("facebookData", obj.getString("picture"))
                }
                handleFacebookAccessToken(result.accessToken)
                var params = Bundle()
                params.putString("fields", "email,public_profile")
                graphRequest.parameters = params
                graphRequest.executeAsync()
            }

            override fun onCancel() {
                Log.d(TAG, "failedLogin")
                progressBar.visibility=View.GONE
            }
            override fun onError(error: FacebookException) {
                Log.d(TAG, "errorLogin")
                progressBar.visibility=View.GONE
            }


        })

/////google button click/////////////

        googleLoginBtn.setOnClickListener {

           //calling sign in method below
            signIn()
        }
/////if custom button click, perform facebook button click/////
     View.OnClickListener{
         if (it==customFbBtn){
             fbLoginBtn.performClick()

         }
     }
//////customFb button click/////
        customFbBtn.setOnClickListener {
            /////code to go to web browser directyly when click login instead of going to facebook App
            LoginManager.getInstance()
                .setLoginBehavior(LoginBehavior.WEB_ONLY)
            /////permissions of info of user we can get when he/she logs in
            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        }



    }//oncreate



/////////Google Login Methods//////////////////////
    val RC_SIGN_IN= 56

//sign in method
    private fun signIn() {


        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


//////////activity result of facebook and google login seperated by if and else////////////

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        auth = FirebaseAuth.getInstance()
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
                progressBar.visibility=View.VISIBLE
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
//////code for facebook on activity result
        else{
            cbManager.onActivityResult(requestCode, resultCode, data)


        }
    }
//////logging in with google credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    progressBar.visibility=View.GONE

                    val userId= auth.currentUser?.uid
///token for notification
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.d(TAG, "Fetching FCM registration token failed", task.exception)

                        }

                       token = task.result
                        val LoginModel= LoginModel(profileName = user!!.displayName .toString(),
                            profileUrl = user?.photoUrl.toString(),
                        token = token.toString())
                        val documentReference: DocumentReference= fStore.collection("users").document(userId.toString())
                        documentReference.set(LoginModel).addOnSuccessListener {
                                  }
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Welcome "+ user!!.displayName , Toast.LENGTH_SHORT).show()


                    })

///////////uploading user data to firestore

                }
             else {
            // If sign in fails, display a message to the user.
            Log.w("TAG", "signInWithCredential:failure", task.exception)
                    progressBar.visibility=View.GONE

        }

    }
    }

////////checks if any user is logged in through any firebase authentication method////////
    override fun onStart() {
        super.onStart()
        if( auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            overridePendingTransition(0, 0)
            startActivity(intent)
        }}

////////////FACebook handle acccess token//////////////

    private fun handleFacebookAccessToken(token: AccessToken) {

        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential: AuthCredential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Welcome "+ user!!.displayName,
                        Toast.LENGTH_SHORT).show()

                    ///////////adding user to firstore/////////
                    val userId= auth.currentUser?.uid
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.d(TAG, "Fetching FCM registration token failed", task.exception)

                        }

                       val tkn = task.result
                        val LoginModel = LoginModel(
                            profileName = user!!.displayName.toString(),
                            profileUrl = user?.photoUrl.toString(),
                            tkn.toString()
                        )
                        val documentReference: DocumentReference =
                            fStore.collection("users").document(userId.toString())
                        documentReference.set(LoginModel).addOnSuccessListener {
                        }

                        progressBar.visibility = View.GONE

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)

                    })
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    progressBar.visibility=View.GONE
                }
            }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

}