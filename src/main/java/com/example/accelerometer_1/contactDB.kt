package com.example.accelerometer_1

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [contact::class], version = 1)
abstract class contactDataBase:RoomDatabase() {

    abstract fun contactDao():contactDao
}