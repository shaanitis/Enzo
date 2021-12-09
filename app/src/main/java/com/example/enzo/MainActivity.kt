package com.example.enzo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
             this.supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val navBar: BottomNavigationView = findViewById(R.id.navBar)
        navBar.setItemIconTintList(null);
        val navController = findNavController(R.id.fragmentHost)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFrag, R.id.chatFrag, R.id.addFrag, R.id.notifyFrag, R.id.profileFrag
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navBar.setupWithNavController(navController)

    }//onCreate

    override fun onBackPressed() {
        finishAffinity();
    }
}//Main