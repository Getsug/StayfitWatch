package com.krono.stayfit.presentation

import android.util.Log
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@AndroidEntryPoint
class PassiveDataService : PassiveListenerService() {

    @Inject
    lateinit var repository: PassiveDataRepository
     private val userId = "Dev001"

    private val database = Firebase.firestore


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

                Log.d(PASSIVE_DATA_TAG, "heart rate value is $it")

                //val user = User(userId, it)
                addUserData(heartRate = it, date = currentDate, measuredDataType = "Heart Rate", currentTime)
                Log.d(PASSIVE_DATA_TAG, "user data added")
            }


        }

    }


    private fun addUserData(heartRate: Double, date: String, measuredDataType: String, time: String){


        val maxOrTotalRef = database
            .collection("users").document("$userId")
            .collection("maxOrTotal").document("$date")

        val heartRateDocumentRef = database
            .collection("users").document("$userId")
            .collection("$measuredDataType").document("$date")


        val recordedHeartRate = hashMapOf(
            "time" to "$time",
            "heartRate" to "$heartRate"
        )




        heartRateDocumentRef.collection("measuredValueAndTime").document()
            .set(recordedHeartRate)

        //TODO: order  measuredValueAndTime by time
        //heartRateDocumentRef.collection("measuredValueAndTime")

        maxOrTotalRef.get()
            .addOnSuccessListener { document ->

                if(document.exists()){
                    val maxHeartRate = document.data?.get("maxHeartRate")

                    Log.d("DB maxHeartRate", "current max  is $maxHeartRate")

                    
                    /**
                     * maxHeartRate is sometimes null.
                     *surrounding it with a null check avoids errors during conversion to double
                    */
                    if(maxHeartRate != null){
                        if(heartRate >= maxHeartRate.toString().toDouble()){
                            val newMax = hashMapOf(
                                "time" to "$time",
                                "maxHeartRate" to "$heartRate"
                            )
                            maxOrTotalRef.set(newMax)

                            Log.d("DB maxHeartRate", "new max  is $heartRate")
                        }
                    }

                }
                else{
                    Log.d("maxOrTotal", "no such document")
                    val recordedMaxOrTotal = hashMapOf(
                        "time" to "$time",
                        "maxHeartRate" to "$heartRate"
                    )

                    maxOrTotalRef.set(recordedMaxOrTotal)
                }

            }
            .addOnFailureListener {exception ->
                Log.d("maxOrTotal", "get failed with", exception)
            }

    }

}
