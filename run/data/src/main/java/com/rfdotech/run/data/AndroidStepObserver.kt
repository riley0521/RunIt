package com.rfdotech.run.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.rfdotech.run.domain.StepObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidStepObserver(
    private val context: Context
) : StepObserver {

    override fun observeSteps(currentSteps: Int): Flow<Int> {
        return callbackFlow {
            val sensorManager = context.getSystemService(SensorManager::class.java)
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            if (sensor == null) {
                close()
                return@callbackFlow
            }

            var step = currentSteps
            var lastStepTimestamp = 0L

            val listener = object: SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null || event.sensor.type != sensor.type) {
                        return
                    }

                    if (event.timestamp > lastStepTimestamp) {
                        step += 1
                        lastStepTimestamp = event.timestamp

                        trySend(step)
                    }
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit
            }

            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)

            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}