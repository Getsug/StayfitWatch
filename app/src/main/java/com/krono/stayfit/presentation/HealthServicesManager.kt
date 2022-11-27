package com.krono.stayfit.presentation
import android.util.Log
import androidx.concurrent.futures.await
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig


class HealthServicesManager (healthServicesClient: HealthServicesClient) {

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

//val healthClient = HealthServices.getClient(this /*context*/)
//val passiveMonitoringClient = healthClient.passiveMonitoringClient
//lifecycleScope.launchWhenCreated {
//    val capabilities = passiveMonitoringClient.capabilities.await()
//    // Supported types for passive data collection
//    supportsHeartRate =
//        DataType.HEART_RATE_BPM in capabilities.supportedDataTypesPassiveMonitoring
//    // Supported types for PassiveGoals
//    supportsStepsGoal =
//        DataType.STEPS_DAILY in capabilities.supportedDataTypesPassiveGoals
//}