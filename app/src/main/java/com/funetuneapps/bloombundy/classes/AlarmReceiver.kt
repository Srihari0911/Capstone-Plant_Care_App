package com.funetuneapps.bloombundy.classes

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.funetuneapps.bloombundy.MainActivity
import com.funetuneapps.bloombundy.R
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null && intent != null) {
            val plantName = intent.getStringExtra("name")
            val id = intent.getIntExtra("id", 0)
            val days = intent.getIntExtra("days", 2)
            val plantId = intent.getStringExtra("plantId").toString()

            when (intent.getIntExtra("type", 0)) {
                1 -> {
                    updateDatabaseWater(plantId)
                    makeNotification(
                        context,
                        "Your plant $plantName is missing you",
                        "Don't forget to water it today",
                    )
                    setAlarmAgain1(context, plantName.toString(), id, plantId,days)
                }

                2 -> {
                    updateDatabaseSunlight(plantId)
                    makeNotification(
                        context,
                        "Your plant $plantName is missing you",
                        "Don't forget to provide it sunlight today"
                    )
                    setAlarmAgain2(context, plantName.toString(), id, plantId,days)

                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun makeNotification(
        context: Context,
        title: String,
        text: String,
        isFriendNotify: Int = 1
    ) {
        try {
            val notifyIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val notifyPendingIntent = PendingIntent.getActivity(
                context,
                0,
                notifyIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification: NotificationCompat.Builder =
                NotificationCompat.Builder(context, "BloomBuddy").apply {
                    this.setSmallIcon(R.drawable.app_icon)
                    this.setContentTitle(title)
                    this.setContentText(text)
                    this.setContentIntent(notifyPendingIntent)
                    this.color = ContextCompat.getColor(context, R.color.primary)
                    this.setAutoCancel(true)
                }
            val notificationManager =
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "BloomBuddy",
                    "Plant Care Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.setShowBadge(true)
                notificationManager.createNotificationChannel(channel)
            }
            with(NotificationManagerCompat.from(context)) {
                notify((1000..2343).random(), notification.build())
            }
        } catch (e: Exception) {

        }

    }

    private fun updateDatabaseWater(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseFirestore.getInstance().collection("plants").document(id)
                .update("waterTime", System.currentTimeMillis())
        }
    }

    private fun updateDatabaseSunlight(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseFirestore.getInstance().collection("plants").document(id)
                .update("sunlightTime", System.currentTimeMillis())
        }
    }

    private fun setAlarmAgain1(context: Context, name: String, id: Int, plantId: String,days:Int) {
        val i = Intent(context, AlarmReceiver::class.java)
        i.putExtra("name", name)
        i.putExtra("type", 1)
        i.putExtra("days", days)
        i.putExtra("id", id)
        i.putExtra("plantId", plantId)
        val pi = PendingIntent.getBroadcast(
            context, id, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (days * AlarmManager.INTERVAL_DAY), pi
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (days * AlarmManager.INTERVAL_DAY), pi
                )
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (days * AlarmManager.INTERVAL_DAY), pi
            )
        }
    }

    private fun setAlarmAgain2(context: Context, name: String, id: Int, plantId: String,days:Int) {
        val i = Intent(context, AlarmReceiver::class.java)
        i.putExtra("name", name)
        i.putExtra("type", 2)
        i.putExtra("days", days)
        i.putExtra("id", id)
        i.putExtra("plantId", plantId)
        val pi = PendingIntent.getBroadcast(
            context, id, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (days * AlarmManager.INTERVAL_DAY), pi
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (days * AlarmManager.INTERVAL_DAY), pi
                )
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (days * AlarmManager.INTERVAL_DAY), pi
            )
        }
    }


}