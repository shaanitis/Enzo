package com.example.enzo.NotificationWork

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.enzo.BuyerActivity
import com.example.enzo.ChattingScreen
import com.example.enzo.MainActivity
import com.example.enzo.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


private const val CHANNEL_ID="newChannel"
private const val channelName="channelName"
class FirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent= Intent(this, MainActivity::class.java)
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID= Random.nextInt()

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
    val notification= NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(message.data["title"])
        .setContentText(message.data["message"])
        .setSmallIcon(R.drawable.saveicon)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

        notificationManager.notify(notificationID, notification)
    }

   @RequiresApi(Build.VERSION_CODES.O)
   private fun createNotificationChannel(notificationManager: NotificationManager){

       val channel=NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
           description="My Channel Description"
       }
       notificationManager.createNotificationChannel(channel)

    }
}