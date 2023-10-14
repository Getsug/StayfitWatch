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
    private val stepsDaily = setOf(DataType.STEPS_DAILY)
    private val passiveDataTypes = setOf(
        DataType.HEART_RATE_BPM,
        DataType.STEPS_DAILY,
    )



    suspend fun registerPassiveData() {

        //determine which data types to receive
        val passiveListenerConfig = PassiveListenerConfig.builder()
            .setDataTypes(passiveDataTypes)
            .build()

        //Using a service for passive listening
        Log.i(PASSIVE_DATA_TAG, "Passive Listener Registered")
        passiveMonitoringClient.setPassiveListenerServiceAsync(
            PassiveDataService::class.java,
            passiveListenerConfig
        ).await()
    }

    suspend fun unregisterForPassiveData() {
        Log.i(PASSIVE_DATA_TAG,"Passive Listener Unregistered")
        passiveMonitoringClient.clearPassiveListenerServiceAsync().await()
    }


    /****** heart rate ******/
    suspend fun supportsHeartRate(): Boolean {
        val capabilities = passiveMonitoringClient.getCapabilitiesAsync().await()
        return (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesPassiveMonitoring)
    }



    suspend fun registerForHeartRateData() {

        //determine which data types to receive
        val passiveListenerConfig = PassiveListenerConfig.builder()
            .setDataTypes(dataTypes)
            .build()

        //Using a service for passive listening
        Log.i(PASSIVE_DATA_TAG, "Passive Heart Rate Listener Registered")
        passiveMonitoringClient.setPassiveListenerServiceAsync(
            PassiveDataService::class.java,
            passiveListenerConfig
        ).await()
    }

    suspend fun unregisterForHeartRateData() {
        Log.i(PASSIVE_DATA_TAG,"Passive Heart Rate Listener Unregistered")
        passiveMonitoringClient.clearPassiveListenerServiceAsync().await()
    }


    /****** daily steps  ******/
    suspend fun supportsDailySteps(): Boolean {
        val capabilities = passiveMonitoringClient.getCapabilitiesAsync().await()
        return (DataType.STEPS_DAILY in capabilities.supportedDataTypesPassiveMonitoring)
    }


    suspend fun registerForStepsDaily() {
        val passiveListenerConfig = PassiveListenerConfig.builder()
            .setDataTypes(stepsDaily)
            .build()

        //Using a service for passive listening
        Log.i(PASSIVE_DATA_TAG, "Passive Steps Daily Listener Registered")
        passiveMonitoringClient.setPassiveListenerServiceAsync(
            PassiveDataService::class.java,
            passiveListenerConfig
        ).await()
    }


    suspend fun unregisterForStepsDaily() {
        Log.i(PASSIVE_DATA_TAG,"Passive Steps Daily Listener Unregistered")
        passiveMonitoringClient.clearPassiveListenerServiceAsync().await()
    }


}
