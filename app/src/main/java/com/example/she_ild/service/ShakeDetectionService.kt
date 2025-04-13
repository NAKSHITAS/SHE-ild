package com.example.she_ild.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import com.example.she_ild.ShakeDetector
import com.example.she_ild.ui.theme.SHEildViewModel
import androidx.core.app.NotificationCompat


class ShakeDetectionService : Service() {

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        shakeDetector = ShakeDetector(
            onShake = {
                val viewModel = SHEildViewModel() // Still a bad idea—see below!
                viewModel.onShakeDetected(this)
            },
            thresholdProvider = { 2.7f } // default for now, or load from shared pref later
        )


        registerShakeDetector()
        startForegroundServiceWithNotification()
    }

    private fun registerShakeDetector() {
        sensorManager.registerListener(
            shakeDetector,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    private fun unregisterShakeDetector() {
        sensorManager.unregisterListener(shakeDetector)
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "shake_channel_id"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Shake Detection Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("SHE-ild Active")
            .setContentText("Shake detection is running")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()


        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterShakeDetector()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
