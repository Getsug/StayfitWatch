package com.krono.stayfit.presentation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.HeartRateAccuracy
import androidx.health.services.client.data.HeartRateAccuracy.SensorStatus.Companion.ACCURACY_HIGH
import androidx.health.services.client.data.HeartRateAccuracy.SensorStatus.Companion.ACCURACY_MEDIUM
import androidx.health.services.client.data.SampleDataPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_FILENAME)

class PassiveDataRepository @Inject constructor (private val dataStore: DataStore<Preferences>) {

    companion object {
        //const val PREFERENCES_FILENAME = "passive_data_prefs"
        //private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_FILENAME)
        private val PASSIVE_DATA_ENABLED = booleanPreferencesKey("passive_data_enabled")
        private val LATEST_HEART_RATE = doublePreferencesKey("latest_heart_rate")


    }


    val getPassiveDataEnabledFlow: Flow<Boolean> = dataStore.data.map { pref ->
        pref[PASSIVE_DATA_ENABLED] ?: false
    }

    suspend fun setPassiveDataEnabled(enabled: Boolean) {
        dataStore.edit { pref ->
            pref[PASSIVE_DATA_ENABLED] = enabled
        }
    }


    val getLatestHeartRateFlow: Flow<Double> = dataStore.data.map { pref ->
        pref[LATEST_HEART_RATE] ?: 0.0
    }

    suspend fun storeLatestHearRate(heartRate: Double) {
        dataStore.edit { pref ->
            pref[LATEST_HEART_RATE] = heartRate
        }
    }

//    companion object {
//        //const val PREFERENCES_FILENAME = "passive_data_prefs"
//        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_FILENAME)
//        private val PASSIVE_DATA_ENABLED = booleanPreferencesKey("passive_data_enabled")
//        private val LATEST_HEART_RATE = doublePreferencesKey("latest_heart_rate")
//
//
//    }

}

// TODO: get latestHearRate from DataContainer then store in a DataStore
fun List<SampleDataPoint<Double>>.latestHeartRate(): Double? {
    return this
        // dataPoints can have multiple types (e.g. if the app is registered for multiple types).
        .filter { it.dataType == DataType.HEART_RATE_BPM }
        // where accuracy information is available, only show readings that are of medium or
        // high accuracy. (Where accuracy information isn't available, show the reading if it is
        // a positive value).
        .filter {
            it.accuracy == null ||
                    setOf(
                        ACCURACY_HIGH,
                        ACCURACY_MEDIUM
                    ).contains((it.accuracy as HeartRateAccuracy).sensorStatus)
        }
        .filter {
            it.value > 0
        }
        // HEART_RATE_BPM is a SAMPLE type, so start and end times are the same.
        .maxByOrNull { it.timeDurationFromBoot }?.value
}