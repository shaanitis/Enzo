package com.example.enzo

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.ChangeBounds
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.enzo.Fragments.SelectCategoryFrag
import com.example.enzo.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
             this.supportActionBar?.hide()
        setContentView(R.layout.activity_main)




        val navBar: BottomNavigationView = findViewById(R.id.navBar)
        

        val navController = findNavController(R.id.fragmentHost)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFrag, R.id.chatFrag, R.id.adCategory, R.id.notifyFrag, R.id.profileFrag
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navBar.setupWithNavController(navController)





       ////to prevent reloading of data on reclicking navbar icon
        ///doing nothing on navbar item reselected
          navBar.setOnItemReselectedListener {
              Log.d("", "")
          }


    }//onCreate



    public class HideKeyboard {
        companion object c {
            fun hideKeyboard(activity: Activity) {
                val imm =
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                //Find the currently focused view, so we can grab the correct window token from it.
                var view: View? = activity.currentFocus
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = View(activity)
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }
    }

}//Main