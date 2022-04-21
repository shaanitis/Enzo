package com.example.enzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashEnzo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_enzo)
  this.supportActionBar?.hide()
       lifecycleScope.launch {
           delay(2000L)
           val intent= Intent(this@SplashEnzo, WelcomeScreen::class.java)
           startActivity(intent)
       }


    }
}