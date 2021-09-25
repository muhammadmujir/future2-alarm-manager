package com.example.alarmapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
  companion object{
    private const val NOTIFICATION_ID = 0
    private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
  }
  private lateinit var viewBinding: ActivityMainBinding
  private var mNotificationManager: NotificationManager? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(viewBinding.root)
    mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val alarmManager: AlarmManager? = getSystemService(ALARM_SERVICE) as? AlarmManager
    val notifyIntent = Intent(this, AlarmReceiver::class.java)
    checkAlarmState(notifyIntent)
    val notifyPendingIntent = PendingIntent.getBroadcast(
      this,
      NOTIFICATION_ID,
      notifyIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )
    viewBinding.btnNextAlarm.setOnClickListener {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        alarmManager?.nextAlarmClock?.triggerTime?.let {
          Toast.makeText(this, "Time: "+timeStampToDate(it), Toast.LENGTH_SHORT).show()
        }
      }
    }
    viewBinding.alarmToggle.setOnCheckedChangeListener { compoundButton, isChecked ->
      var toastMessage = ""
      if (isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          alarmManager?.setAlarmClock(
            AlarmManager.AlarmClockInfo(
              Calendar.getInstance().timeInMillis + 15 * 60 *1000,
              notifyPendingIntent
            ), notifyPendingIntent)
          toastMessage = "Stand Up Alarm On!";
        }
      } else {
        alarmManager?.cancel(notifyPendingIntent)
        mNotificationManager?.cancelAll()
        toastMessage = "Stand Up Alarm Off!";
      }
      Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }
    createNotificationChannel()
  }

  fun createNotificationChannel() {

    // Create a notification manager object.
    mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    // Notification channels are only available in OREO and higher.
    // So, add a check on SDK version.
    if (Build.VERSION.SDK_INT >=
      Build.VERSION_CODES.O
    ) {

      // Create the NotificationChannel with all the parameters.
      val notificationChannel = NotificationChannel(
        PRIMARY_CHANNEL_ID,
        "Stand up notification",
        NotificationManager.IMPORTANCE_HIGH
      )
      notificationChannel.enableLights(true)
      notificationChannel.lightColor = Color.RED
      notificationChannel.enableVibration(true)
      notificationChannel.description = "Notifies every 15 minutes to stand up and walk"
      mNotificationManager?.createNotificationChannel(notificationChannel)
    }
  }

  private fun checkAlarmState(intent: Intent){
    viewBinding.alarmToggle.isChecked = PendingIntent.getBroadcast(
      this, NOTIFICATION_ID, intent,
      PendingIntent.FLAG_NO_CREATE
    ) != null
  }

  private fun timeStampToDate(timeStamp: Long, pattern: String = "E, dd MMM yyyy HH:mm"): String{
    return SimpleDateFormat(pattern, Locale.getDefault()).format(timeStamp)
  }
}