package com.krono.stayfit.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.health.services.client.HealthServices
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


/**
 * Background data subscriptions are not persisted across device restarts. This receiver checks if
 * we enabled background data and, if so, registers again.
 */
@AndroidEntryPoint
class StartupReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: PassiveDataRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        runBlocking {
            if(repository.getPassiveDataEnabledFlow.first()) {
                val sensorEnabled = context.checkSelfPermission(android.Manifest.permission.BODY_SENSORS)

                if (sensorEnabled == PackageManager.PERMISSION_GRANTED) {

                    // TODO: check more permissions for startup receiver first.
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


@HiltWorker
class RegisterForPassiveDataWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val healthServicesManager: HealthServicesManager
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
