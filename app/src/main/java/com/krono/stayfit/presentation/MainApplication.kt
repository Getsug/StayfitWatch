package com.krono.stayfit.presentation

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


/**
 * Application class, needed to enable dependency injection with Hilt. It also is used to initialize
 * WorkManager.
 */
@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES_FILENAME)