package com.funetuneapps.bloombundy

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.funetuneapps.bloombundy.classes.AlarmReceiver
import com.funetuneapps.bloombundy.databinding.ActivityMainBinding
import com.funetuneapps.bloombundy.models.PlantModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun setAlarmWater(model: PlantModel) {
        val i = Intent(this, AlarmReceiver::class.java)
        i.putExtra("name", model.name)
        i.putExtra("type", 1)
        i.putExtra("days", model.waterDays)
        val id = (1..10000).random()
        i.putExtra("id", id)
        i.putExtra("plantId", model.id)
        val pi = PendingIntent.getBroadcast(
            this, id, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager: AlarmManager =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (model.waterDays * AlarmManager.INTERVAL_DAY), pi
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (model.waterDays * AlarmManager.INTERVAL_DAY), pi
                )
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (model.waterDays * AlarmManager.INTERVAL_DAY), pi
            )
        }
    }

    fun setAlarmSunlight(model: PlantModel) {
        val i = Intent(this, AlarmReceiver::class.java)
        i.putExtra("name", model.name)
        i.putExtra("type", 2)
        i.putExtra("days", model.sunDays)
        val id = (1..10000).random()
        i.putExtra("id", id)
        i.putExtra("plantId", model.id)
        val pi = PendingIntent.getBroadcast(
            this, id, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager: AlarmManager =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (model.sunDays * AlarmManager.INTERVAL_DAY), pi
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (model.sunDays * AlarmManager.INTERVAL_DAY), pi
                )
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (model.sunDays * AlarmManager.INTERVAL_DAY), pi
            )
        }
    }
}