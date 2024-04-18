package com.example.accelerometer_1

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.accelerometer_1.contact
import com.example.accelerometer_1.contactDataBase

class prediction : AppCompatActivity() {
    private lateinit var sensorDataList: LiveData<List<contact>>
    private lateinit var chartX: LineChart
    private lateinit var chartY: LineChart
    private lateinit var chartZ: LineChart
    private lateinit var database: contactDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)

        chartX = findViewById(R.id.chart1)
        chartY = findViewById(R.id.chart2)
        chartZ = findViewById(R.id.chart3)

        database = Room.databaseBuilder(
            applicationContext,
            contactDataBase::class.java,
            "contactDB"
        ).build()

        populateCharts()
    }

    private fun populateCharts() {
        sensorDataList = database.contactDao().getContact()

        sensorDataList.observe(this) { sensorData ->
            val entriesXActual = ArrayList<Entry>()
            val entriesYActual = ArrayList<Entry>()
            val entriesZActual = ArrayList<Entry>()
            val entriesXPredicted = ArrayList<Entry>()
            val entriesYPredicted = ArrayList<Entry>()
            val entriesZPredicted = ArrayList<Entry>()

            var index = 0
            for (sensor in sensorData) {
                entriesXActual.add(Entry(index.toFloat(), sensor.x.toFloat()))
                entriesYActual.add(Entry(index.toFloat(), sensor.y.toFloat()))
                entriesZActual.add(Entry(index.toFloat(), sensor.z.toFloat()))

                // Use your prediction logic here instead of generating random values
                val predictedX = predictValue(sensor.x.toFloat())
                val predictedY = predictValue(sensor.y.toFloat())
                val predictedZ = predictValue(sensor.z.toFloat())

                entriesXPredicted.add(Entry((index + 1).toFloat(), predictedX.toFloat()))
                entriesYPredicted.add(Entry((index + 1).toFloat(), predictedY.toFloat()))
                entriesZPredicted.add(Entry((index + 1).toFloat(), predictedZ.toFloat()))

                index++
            }

            val dataSetXActual = LineDataSet(entriesXActual, "Actual X")
            val dataSetYActual = LineDataSet(entriesYActual, "Actual Y")
            val dataSetZActual = LineDataSet(entriesZActual, "Actual Z")
            val dataSetXPredicted = LineDataSet(entriesXPredicted, "Predicted X")
            val dataSetYPredicted = LineDataSet(entriesYPredicted, "Predicted Y")
            val dataSetZPredicted = LineDataSet(entriesZPredicted, "Predicted Z")

            dataSetXPredicted.color = Color.RED
            dataSetYPredicted.color = Color.GREEN
            dataSetZPredicted.color = Color.BLUE

            val lineDataX = LineData(dataSetXActual, dataSetXPredicted)
            val lineDataY = LineData(dataSetYActual, dataSetYPredicted)
            val lineDataZ = LineData(dataSetZActual, dataSetZPredicted)

            chartX.data = lineDataX
            chartY.data = lineDataY
            chartZ.data = lineDataZ

            chartX.invalidate()
            chartY.invalidate()
            chartZ.invalidate()
        }
    }

    // Replace this function with your prediction logic
    private fun predictValue(value: Float): Double {
        val randomDelta = (Math.random() * 2) - 1 // Generates a random value between -1 and +1
        val newValue = value + randomDelta // Add or subtract the random delta from the input value
        return newValue
    }

}
