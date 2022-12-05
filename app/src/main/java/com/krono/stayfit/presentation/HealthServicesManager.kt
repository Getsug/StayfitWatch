package com.krono.stayfit.presentation


import android.util.Log
import androidx.concurrent.futures.await
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import javax.inject.Inject


class HealthServicesManager @Inject constructor (healthServicesClient: HealthServicesClient) {

    private val passiveMonitoringClient = healthServicesClient.passiveMonitoringClient
    private val dataTypes = setOf(DataType.HEART_RATE_BPM)


    suspend fun supportsHeartRate(): Boolean {
        val capabilities = passiveMonitoringClient.getCapabilitiesAsync().await()
        return (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesPassiveMonitoring)
    }

    //determine which data types to receive
    private val passiveListenerConfig = PassiveListenerConfig.builder()
        .setDataTypes(dataTypes)
        .build()

    suspend fun registerForHeartRateData() {
        //Using a service for passive listening
        Log.i(PASSIVE_DATA_TAG, "Passive Listener Registered")
        passiveMonitoringClient.setPassiveListenerServiceAsync(
            PassiveDataService::class.java,
            passiveListenerConfig
        ).await()
    }

    suspend fun unregisterForHeartRateData() {
        Log.i(PASSIVE_DATA_TAG,"Passive Listener Unregistered")
        passiveMonitoringClient.clearPassiveListenerServiceAsync().await()
    }
}
