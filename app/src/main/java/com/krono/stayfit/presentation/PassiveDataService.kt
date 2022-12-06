package com.krono.stayfit.presentation

import android.util.Log
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@AndroidEntryPoint
class PassiveDataService : PassiveListenerService() {

    @Inject
    lateinit var repository: PassiveDataRepository
     private val userId = "Dev001"

    private val database = Firebase.database.reference

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        // TODO: do something with dataPoints

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalDateTime.now().format(timeFormatter)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDateTime.now().format(dateFormatter)

        runBlocking {
            Log.d(PASSIVE_DATA_TAG, "New heart rate value stored")

            dataPoints.getData(DataType.HEART_RATE_BPM).latestHeartRate()?.let {
                repository.storeLatestHearRate(it)

                val user = User(userId, it)
                addUserData(user = user, date = currentDate, measuredDataType = "Heart Rate", currentTime)
            }


        }

    }


    fun addUserData(user: User, date: String, measuredDataType: String, time: String){
        var key = "users/${user.uid}/${date}/${measuredDataType}/${time}/"
        database.child(key).setValue(user)
    }

}
