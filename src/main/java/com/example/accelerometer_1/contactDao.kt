package com.example.accelerometer_1

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


class LiveDataForPrediction<T> : LiveData<T>()

@Dao
interface contactDao {
    @Insert
    suspend fun insertcontact(contact: contact)

    @Update
    suspend fun updatecontact(contact: contact)

    @Delete
    suspend fun deletecontact(contact: contact)

    @Query("SELECT * FROM contactDB ORDER BY id DESC LIMIT 20")
    fun getContact(): LiveData<List<contact>>

//    @Query("SELECT * FROM contactDB ORDER BY id DESC LIMIT 500")
//    fun getContactForPrediction(): LiveDataForPrediction<List<contact>>
}
