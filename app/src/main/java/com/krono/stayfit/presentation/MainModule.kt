package com.krono.stayfit.presentation

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Hilt module that provides singleton (application-scoped) objects.
 */
@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Singleton
    @Provides
    fun provideHealthServicesClient(@ApplicationContext context: Context): HealthServicesClient =
        HealthServices.getClient(context)

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) = context.dataStore

//    @Singleton
//    @Provides
//    fun provideDataBaseReference(@ApplicationContext context: Context) = Firebase.database.reference
}