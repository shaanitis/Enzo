package com.example.enzo.NotificationWork

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.example.enzo.R
import com.example.enzo.WelcomeScreen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ServiceJava : FirebaseMessagingService() {
    var mNotificationManager: NotificationManager?=null


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        val resourceImage = resources.getIdentifier(
            remoteMessage.getNotification()?.getIcon(),
            "drawable",
            packageName
        )
        val builder= NotificationCompat.Builder(this, "CHANNEL_ID")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder.setSmallIcon(R.drawable.egnoti)
        } else {

            builder.setSmallIcon(R.drawable.egnoti)
        }
        val resultIntent = Intent(this, WelcomeScreen::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentTitle(remoteMessage.getNotification()?.getTitle())
        builder.setContentText(remoteMessage.getNotification()?.getBody())
         builder.setSmallIcon(R.drawable.egnoti)
        builder.setContentIntent(pendingIntent)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification()
            ?.getBody()))
        builder.setAutoCancel(true)
        builder.setPriority(Notification.PRIORITY_MAX)
        mNotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channelId"
            val channel = NotificationChannel(
                channelId,
                "Chats",NotificationManager.IMPORTANCE_MAX
            )
            mNotificationManager!!.createNotificationChannel(channel)
            builder.setChannelId(channelId)

        }


// notificationId is a unique int for each notification that you must define
        mNotificationManager!!.notify(100, builder.build())
    }
}