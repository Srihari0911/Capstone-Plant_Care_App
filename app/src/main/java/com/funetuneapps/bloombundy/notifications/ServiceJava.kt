package com.funetuneapps.bloombundy.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.funetuneapps.bloombundy.MainActivity
import com.funetuneapps.bloombundy.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class    ServiceJava : FirebaseMessagingService() {
    private var mNotificationManager: NotificationManager? = null


    @SuppressLint("DiscouragedApi")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        val builder = NotificationCompat.Builder(this, "CHANNEL_NOTI")

        builder.setSmallIcon(R.drawable.app_icon)
        val resultIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentTitle(remoteMessage.notification?.title)
        builder.setContentText(remoteMessage.notification?.body)
        builder.setSmallIcon(R.drawable.app_icon)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        mNotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channelId"
            val channel = NotificationChannel(
                channelId, "Chats", NotificationManager.IMPORTANCE_LOW
            )
            mNotificationManager?.createNotificationChannel(channel)
            builder.setChannelId(channelId)

        }


// notificationId is a unique int for each notification that you must define
        mNotificationManager!!.notify((1..1000).random(), builder.build())
    }
}