package com.krono.stayfit.presentation

import android.util.Log
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class PassiveDataService : PassiveListenerService() {

    @Inject
    lateinit var repository: PassiveDataRepository

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        // TODO: do something with dataPoints
        runBlocking {
            Log.d(PASSIVE_DATA_TAG, "New heart rate value stored")
            dataPoints.getData(DataType.HEART_RATE_BPM).latestHeartRate()?.let {
                repository.storeLatestHearRate(it)
            }
        }

    }

}
