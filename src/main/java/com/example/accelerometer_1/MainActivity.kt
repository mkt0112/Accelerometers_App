package com.example.accelerometer_1

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var database: contactDataBase
    private lateinit var sensorManager: SensorManager
    private lateinit var xValue: TextView
    private lateinit var yValue: TextView
    private lateinit var zValue: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonOpenChart = findViewById<Button>(R.id.button_open_chart)
        buttonOpenChart.setOnClickListener {
            val intent = Intent(this, graph::class.java)
            startActivity(intent)
        }

        val buttonpre= findViewById<Button>(R.id.buttonPrediction)
        buttonpre.setOnClickListener {
            val intent = Intent(this, prediction::class.java)
            startActivity(intent)
        }



        // Initialize the Room database
        database = Room.databaseBuilder(
            applicationContext,
            contactDataBase::class.java,
            "contactDB"
        ).build()

        // Initialize the TextViews from the layout
        xValue = findViewById(R.id.x_value)
        yValue = findViewById(R.id.y_value)
        zValue = findViewById(R.id.z_value)

        // Get the SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Get the accelerometer sensor from the sensor manager
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Register the listener
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Start saving data every 30 seconds
        startSavingData()
    }

    private fun startSavingData() {
        lifecycleScope.launch {
            while (true) {
                delay(2000) // Delay for 30 seconds
                saveAccelerometerData()
            }
        }
    }

    private fun saveAccelerometerData() {
        val x = xValue.text.toString().toFloatOrNull() ?: return
        val y = yValue.text.toString().toFloatOrNull() ?: return
        val z = zValue.text.toString().toFloatOrNull() ?: return
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val accelerometerData = contact(0, currentTime, x.toString(), y.toString(), z.toString())
        lifecycleScope.launch(Dispatchers.IO) {
            database.contactDao().insertcontact(accelerometerData)
            saveDataToFile(currentTime, x, y, z)
        }
    }
    private fun saveDataToFile(time: String, x: Float, y: Float, z: Float) {
        try {
            val dataDir = if (filesDir != null) File(filesDir, "accelerometer_data") else File(applicationContext.filesDir, "accelerometer_data")
            if (!dataDir.exists()) {
                dataDir.mkdirs()
            }
            val file = File(dataDir, "data.csv")
            val fileWriter = FileWriter(file, true)
            // If the file is empty, write the CSV header
            if (file.length() == 0L) {
                fileWriter.append("Time,X,Y,Z\n")
            }
            fileWriter.append("$time,$x,$y,$z\n")
            fileWriter.flush()
            fileWriter.close()
        } catch (e: Exception) {
            Log.e("FileError", "Error saving data to file: ${e.message}")
        }
    }







    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // This method can be used to react to changes in sensor accuracy, if needed
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Display the x, y, and z values rounded to three decimal places
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            xValue.text = String.format("%.3f", x)
            yValue.text = String.format("%.3f", y)
            zValue.text = String.format("%.3f", z)
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the listener again when the app resumes
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister the sensor listener when the app is not visible
        sensorManager.unregisterListener(this)
    }
}

