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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
//                    permissions.getOrDefault(Manifest.permission.BACKGROUND, false) -> {
//
//                    }
//                    else -> {
//                     no permission granted
//                    }


                }
            }

        /**** Permission handler ****/
        requestPermission(viewModel)

        setContent {
            WearApp(viewModel)
        }


    }



    private fun requestPermission(viewModel: MainViewModel) {

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
            /*
            *Permissions are checked each the app is launched
            * Therefore, once the sensor permission is granted, each launch edits the DataStore
            * and passiveDataEnabled value is the set to true.
            * This might reset the the user's settings in cases where passive Data was switched off
            * */
            //TODO: find a way to maintain user's passive data setting
            viewModel.togglePassiveData(true)
        }
        else {
            Log.d("BODY_SENSOR", "permission denied")
            viewModel.togglePassiveData(false)
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

    val passiveDataEnabled = viewModel.passiveDataEnabled.collectAsState(initial = false)
    val latestHeartRate = viewModel.latestHeartRate.collectAsState(initial = 0)
    val latestStepsDaily = viewModel.latestStepsDaily.collectAsState(initial = 0L)



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
            StepCounterScreen(latestStepsDaily = latestStepsDaily.value.toString())
        }
        //TODO: display different screen for no heart rate support
        // TODO: add display screen for passive data disabled
        composable(route = Screen.Heart.route) {
            HeartRateScreen(latestHearRate = latestHeartRate.value.toString()) //passive data  enabled screen
        }
        composable(route = Screen.Calories.route) {
            //CaloriesScreen(latestCalories  = latestHeartRate.value.toString()) //passive data  enabled screen
            CaloriesScreen(latestCalories  = "120") //passive data  enabled screen
        }
        composable(route = Screen.Water.route) {
            //WaterCountScreen(latestWaterCount = latestHeartRate.value.toString()) //passive data  enabled screen
            WaterCountScreen(latestWaterCount = "4") //passive data  enabled screen
        }
        composable(route = Screen.Settings.route) {
            //TODO: needs checking
            //SettingsScreen(passiveDataEnabled.value)
            SettingsScreen(viewModel, passiveDataEnabled.value)
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