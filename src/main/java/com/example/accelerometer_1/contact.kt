package com.example.accelerometer_1

import androidx.annotation.LongDef
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="contactDB")
data class contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val time:String,
    val x: String,
    val y: String,
    val z:String
)
