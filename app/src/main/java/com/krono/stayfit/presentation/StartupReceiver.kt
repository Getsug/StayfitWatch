package com.krono.stayfit.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class StartupReceiver : BroadcastReceiver() {

    lateinit var repository: PassiveDataRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        runBlocking {
            if(repository.getPassiveDataEnabledFlow.first()) {
                val sensorEnabled = context.checkSelfPermission(android.Manifest.permission.BODY_SENSORS)

                if (sensorEnabled == PackageManager.PERMISSION_GRANTED) {

                    // TODO: check permissions for startup receiver first.
                    Log.d(PASSIVE_DATA_TAG, "Enqueuing worker")
                    WorkManager.getInstance(context).enqueue(
                        OneTimeWorkRequestBuilder<RegisterForPassiveDataWorker>().build()
                    )

                } else {
                    // We may have lost the permission somehow. Mark that background data is
                    // disabled so the state is consistent the next time the user opens the app UI.
                    repository.setPassiveDataEnabled(false)
                }

            }
        }

    }
}

class RegisterForPassiveDataWorker(
    private val appContext: Context,
    private val healthServicesManager: HealthServicesManager,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d(PASSIVE_DATA_TAG, "Worker running")
        runBlocking {
            healthServicesManager.registerForHeartRateData()

//            HealthServices.getClient(appContext)
//                .passiveMonitoringClient
//                .setPassiveListenerCallback(...)
        }
        return Result.success()
    }
}
