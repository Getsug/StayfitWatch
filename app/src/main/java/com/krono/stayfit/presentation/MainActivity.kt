/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.krono.stayfit.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.HeartRateAccuracy
import androidx.health.services.client.data.HeartRateAccuracy.SensorStatus.Companion.ACCURACY_HIGH
import androidx.health.services.client.data.HeartRateAccuracy.SensorStatus.Companion.ACCURACY_MEDIUM
import androidx.health.services.client.data.SampleDataPoint
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.krono.stayfit.R
import com.krono.stayfit.presentation.theme.StayFitTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
//    private  lateinit var repository: PassiveDataRepository
//    private lateinit var healthServicesManager: HealthServicesManager

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp(viewModel)

            requestPermissionLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    when {
                        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                            // Precise location access granted.
                            //Log.d("ACCESS_FINE_LOCATION", "callback granted")
                        }
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                            // Only approximate location access granted.
                        }
                        permissions.getOrDefault(Manifest.permission.BODY_SENSORS, false) -> {

                        }
                        permissions.getOrDefault(Manifest.permission.ACTIVITY_RECOGNITION, false) -> {

                        }
                        permissions.getOrDefault(Manifest.permission.FOREGROUND_SERVICE, false) -> {

                        }
                        //else -> {
                        // no permission granted
                        //}


                    }
                }

            StayFitTheme {
                requestPermission()
            }

        }

//        lifecycleScope.launchWhenCreated {
//            val supportsHeartRate = healthServicesManager.supportsHeartRate()
//
//            if(supportsHeartRate){
//                Log.d("LISTENER_SERVICE", "Registered")
//                healthServicesManager.registerForHeartRateData()
//            }else {
//                Log.d("LISTENER_SERVICE", "Unregistered")
//                healthServicesManager.unregisterForHeartRateData()
//            }
//
//        }


    }



    private fun requestPermission() {

        //val requestPermissionLauncher = registerPermissionCallBack()

        val requestPermissionList: MutableList<String> = ArrayList()

        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.d("ACCESS_FINE_LOCATION", "permission granted")
        }
        else {
            Log.d("ACCESS_FINE_LOCATION", "permission denied")
            requestPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }


        if(checkPermission(Manifest.permission.ACTIVITY_RECOGNITION)) {
            Log.d("ACTIVITY_RECOGNITION", "permission granted")
        }
        else {
            Log.d("ACTIVITY_RECOGNITION", "permission denied")
            requestPermissionList.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }


        if(checkPermission(Manifest.permission.BODY_SENSORS)) {
            Log.d("BODY_SENSOR", "permission granted")
        }
        else {
            Log.d("BODY_SENSOR", "permission denied")
            requestPermissionList.add(Manifest.permission.BODY_SENSORS)
        }


        if(checkPermission(Manifest.permission.FOREGROUND_SERVICE)) {
            Log.d("FOREGROUND_SERVICE", "permission granted")
        }
        else {
            Log.d("FOREGROUND_SERVICE", "permission denied")
            requestPermissionList.add(Manifest.permission.FOREGROUND_SERVICE)
        }


        if(requestPermissionList.isNotEmpty()){
            requestPermissionLauncher.launch(requestPermissionList.toTypedArray())
        }

    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

}




@Composable
fun WearApp(viewModel: MainViewModel){

    //val viewModel: MainViewModel = viewModel()

    val latestHeartRate = viewModel.latestHeartRate.collectAsState(initial = 0)

    //var name = MutableState
    //navigation to the app screens
    val navController = rememberSwipeDismissableNavController()
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // TODO: build navigation graph
        composable(route = Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Screen.Steps.route) {
            StepCounterScreen()
        }
        //TODO: display different screen for no heart rate support
        // TODO: add display screen for passive data disabled
        composable(route = Screen.Heart.route) {
            HeartRateScreen(latestHearRate = latestHeartRate.value.toString()) //passive data  enabled screen
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }

    }

}




//@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    WearApp()
//}

//@Preview(
//    widthDp = WEAR_PREVIEW_DEVICE_WIDTH_DP,
//    heightDp = WEAR_PREVIEW_DEVICE_HEIGHT_DP,
//    apiLevel = WEAR_PREVIEW_API_LEVEL,
//    uiMode = WEAR_PREVIEW_UI_MODE,
//    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
//    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
//)
//@Composable
//fun WearAppPreview(){
//    WearApp()
//}