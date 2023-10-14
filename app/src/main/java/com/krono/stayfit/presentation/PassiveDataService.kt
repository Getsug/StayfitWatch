package com.krono.stayfit.presentation

import android.util.Log
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import com.google.firebase.firestore.SetOptions
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

        Log.d(PASSIVE_DATA_TAG, "New data point received")

        runBlocking {
            //Log.d(PASSIVE_DATA_TAG, "New heart rate value stored")

            dataPoints.getData(DataType.HEART_RATE_BPM).latestHeartRate()?.let {
                repository.storeLatestHearRate(it)

                Log.d(PASSIVE_DATA_TAG, "heart rate value is $it")

                //val user = User(userId, it)
                addHeartRateData(heartRate = it, date = currentDate, measuredDataType = "Heart Rate", currentTime)
                Log.d(PASSIVE_DATA_TAG, "user data added")
            }


        }

        runBlocking {
            //Log.d(PASSIVE_DATA_TAG, "New heart rate value stored")

            dataPoints.getData(DataType.STEPS_DAILY).latestStepsDaily()?.let {
                repository.storeLatestStepsDaily(it)

                Log.d(PASSIVE_DATA_TAG, "steps daily value is $it")

                //val user = User(userId, it)
                addStepsDailyData(stepsDaily = it, date = currentDate, measuredDataType = "StepsDaily", currentTime)
                Log.d(PASSIVE_DATA_TAG, "user data added")
            }


        }

    }


    private fun addHeartRateData(heartRate: Double, date: String, measuredDataType: String, time: String){


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
//                            val newMax = hashMapOf(
//                                "time" to "$time",
//                                "maxHeartRate" to "$heartRate"
//                            )
                            maxOrTotalRef.update("maxHeartRate", "$heartRate")

                            Log.d("DB maxHeartRate", "new max  is $heartRate")
                        }
                    }
                    else{

                        val newMax = hashMapOf("maxHeartRate" to "$heartRate")
                        //No value of maxHeart rate exists in the db.
                        // New value is set to the received reading
                        maxOrTotalRef.set(newMax, SetOptions.merge())
                        Log.d("DB maxHeartRate", "new max  is $heartRate")
                    }



                }
                else{
                    Log.d("maxOrTotal", "no such document")
                    val recordedMaxOrTotal = hashMapOf(
//                        "time" to "$time",
                        "maxHeartRate" to "$heartRate"
                    )

                    maxOrTotalRef.set(recordedMaxOrTotal)
                }

            }
            .addOnFailureListener {exception ->
                Log.d("maxOrTotal", "get failed with", exception)
            }

    }

    private fun addStepsDailyData(stepsDaily: Long, date: String, measuredDataType: String, time: String){


        val maxOrTotalRef = database
            .collection("users").document("$userId")
            .collection("maxOrTotal").document("$date")

        val stepsDailyDocumentRef = database
            .collection("users").document("$userId")
            .collection("$measuredDataType").document("$date")


        val recordedStepsDaily = hashMapOf(
            "time" to "$time",
            "stepsDaily" to "$stepsDaily"
        )




        stepsDailyDocumentRef.collection("measuredValueAndTime").document()
            .set(recordedStepsDaily)

        //TODO: order  measuredValueAndTime by time
        //heartRateDocumentRef.collection("measuredValueAndTime")

        maxOrTotalRef.get()
            .addOnSuccessListener { document ->

                if(document.exists()){
                    val totalSteps = document.data?.get("totalSteps")

                    Log.d("DB totalSteps", "current total  is $totalSteps")


                    /**
                     * totalSteps is sometimes null.
                     *surrounding it with a null check avoids errors during conversion to long
                     */
                    if(totalSteps != null){
                        if(stepsDaily > totalSteps.toString().toLong()){
//                            val newMax = hashMapOf(
//                                "time" to "$time",
//                                "totalSteps" to "$stepsDaily"
//                            )
                            maxOrTotalRef.update("totalSteps", "$stepsDaily")

                            Log.d("DB totalSteps", "new totalSteps  is $stepsDaily")
                        }
                    }
                    else{
                        //No value of total steps exists in the db.
                        // New value is set to the received reading
                        maxOrTotalRef.update("totalSteps", "$stepsDaily")
                        Log.d("DB totalSteps", "new totalSteps  is $stepsDaily")
                    }

                }
                else{
                    Log.d("maxOrTotal", "no such document")
                    val recordedMaxOrTotal = hashMapOf("totalSteps" to "$stepsDaily")

                    maxOrTotalRef.set(recordedMaxOrTotal, SetOptions.merge())
                }

            }
            .addOnFailureListener {exception ->
                Log.d("maxOrTotal", "get failed with", exception)
            }

    }

}
