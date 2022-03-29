package com.example.enzo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.ChangeBounds
import android.util.Log
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


}//Main