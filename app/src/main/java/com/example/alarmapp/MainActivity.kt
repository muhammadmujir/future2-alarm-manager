package com.example.alarmapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.alarmapp.databinding.ActivityMainBinding


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
    viewBinding.alarmToggle.setOnCheckedChangeListener { compoundButton, isChecked ->
      var toastMessage = ""
      if (isChecked) {
        deliverNotification(this);
        //Set the toast message for the "on" case
        toastMessage = "Stand Up Alarm On!";
      } else {
        //Cancel notification if the alarm is turned off
        mNotificationManager?.cancelAll();
        //Set the toast message for the "off" case
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

  private fun deliverNotification(context: Context) {
    val contentIntent = Intent(context, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      NOTIFICATION_ID,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder: NotificationCompat.Builder =
      NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stand_up)
        .setContentTitle("Stand Up Alert")
        .setContentText("You should stand up and walk around now!")
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
    mNotificationManager?.notify(NOTIFICATION_ID, builder.build());
  }

}