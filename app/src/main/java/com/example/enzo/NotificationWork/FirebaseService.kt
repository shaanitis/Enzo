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
import com.example.enzo.BuyerActivity
import com.example.enzo.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


private const val CHANNEL_ID="newChannel"
class FirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent= Intent(this, BuyerActivity::class.java)
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID= Random.nextInt()

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
    val notification= androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(message.data["title"])
        .setContentText(message.data["message"])
        .setSmallIcon(R.drawable.ic_notify)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

        notificationManager.notify(notificationID, notification)
    }

   @RequiresApi(Build.VERSION_CODES.O)
   private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName="channelName"
       val channel=NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
           description="New Channel Description"
           enableLights(true)
       }
       notificationManager.createNotificationChannel(channel)

    }
}