package com.example.she_ild

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class ShakeDetector(
    private val onShake: () -> Unit,
    private val thresholdProvider: () -> Float

) : SensorEventListener {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private var lastShakeTime = 0L
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]

            // Calculate the change in acceleration
            val deltaX = kotlin.math.abs(x - lastX)
            val deltaY = kotlin.math.abs(y - lastY)
            val deltaZ = kotlin.math.abs(z - lastZ)

            // Calculate total acceleration change
            val acceleration = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
            
            val threshold = thresholdProvider()
            Log.d("ShakeDetector", "Acceleration: $acceleration, Threshold: $threshold")

            if (acceleration > threshold) {
                val now = System.currentTimeMillis()
                if (now - lastShakeTime > 1000) { // Prevent multiple triggers within 1 second
                    lastShakeTime = now
                    Log.d("ShakeDetector", "Shake detected! Triggering emergency action")
                    coroutineScope.launch {
                        onShake()
                    }
                }
            }

            // Update last values
            lastX = x
            lastY = y
            lastZ = z
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}
