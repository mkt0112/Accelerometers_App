package com.example.accelerometer_1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.room.Room


import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class graph: AppCompatActivity() {
    private lateinit var sensorDataList: LiveData<List<contact>>
    private lateinit var chartX: LineChart
    private lateinit var chartY: LineChart
    private lateinit var chartZ: LineChart
    private lateinit var database: contactDataBase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)


        // Initialize LineChart views
        chartX = findViewById(R.id.chart1)
        chartY = findViewById(R.id.chart2)
        chartZ = findViewById(R.id.chart3)

        // Initialize Room database
        database = Room.databaseBuilder(
            applicationContext,
            contactDataBase::class.java,
            "contactDB"
        ).build()

        // Fetch data from the database and populate the charts
        populateCharts()
    }



    private fun populateCharts() {
        // Fetch data from the database
        sensorDataList = database.contactDao().getContact()

        // Observe the LiveData to update the charts when the data changes
        sensorDataList.observe(this) { sensorData ->
            // Separate data for each axis
            val entriesX = ArrayList<Entry>()
            val entriesY = ArrayList<Entry>()
            val entriesZ = ArrayList<Entry>()

            // Extract data for each axis from the list of SensorData
            var index = 0
            for (sensor in sensorData) {
                entriesX.add(Entry(index.toFloat(), sensor.x.toFloat()))
                entriesY.add(Entry(index.toFloat(), sensor.y.toFloat()))
                entriesZ.add(Entry(index.toFloat(), sensor.z.toFloat()))
                index++
            }

            // Create LineDataSets for each set of data
            val dataSetX = LineDataSet(entriesX, "Time vs X")
            val dataSetY = LineDataSet(entriesY, "Time vs Y")
            val dataSetZ = LineDataSet(entriesZ, "Time vs Z")

            // Add LineDataSets to LineData objects
            val lineDataX = LineData(dataSetX)
            val lineDataY = LineData(dataSetY)
            val lineDataZ = LineData(dataSetZ)

            // Set data to each LineChart
            chartX.data = lineDataX
            chartY.data = lineDataY
            chartZ.data = lineDataZ

            // Refresh the charts to display the data
            chartX.invalidate()
            chartY.invalidate()
            chartZ.invalidate()
        }
    }

}

