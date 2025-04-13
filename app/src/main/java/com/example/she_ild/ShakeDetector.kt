package com.example.she_ild

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
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

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event?.values?.get(0) ?: return
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt(x * x + y * y + z * z)
        if (gForce > thresholdProvider()) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > 1000) {
                lastShakeTime = now
                coroutineScope.launch {
                    onShake()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}
