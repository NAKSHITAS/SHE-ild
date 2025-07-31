package com.example.she_ild

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.she_ild.ShakeDetector
import com.example.she_ild.ui.theme.SHEildTheme
import com.example.she_ild.ui.theme.SHEildViewModel
import android.Manifest

class MainActivity : ComponentActivity() {

    private val viewModel: SHEildViewModel by viewModels()

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // ✅ Shake only triggers if the service is running
        shakeDetector = ShakeDetector(
            onShake = {
                Log.d("MainActivity", "Shake callback triggered, service running: ${viewModel.uiState.value.isServiceRunning}")
                if (viewModel.uiState.value.isServiceRunning) {
                    viewModel.onShakeDetected(this)
                } else {
                    Log.d("MainActivity", "Service not running, ignoring shake")
                }
            },
            thresholdProvider = { 
                val threshold = viewModel.shakeThreshold.value
                Log.d("MainActivity", "Current shake threshold: $threshold")
                threshold
            }
        )


        setContent {
            SHEildTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    SHEildNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "Registering shake detector")
        sensorManager.registerListener(
            shakeDetector,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "Unregistering shake detector")
        sensorManager.unregisterListener(shakeDetector)
    }

}

