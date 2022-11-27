package com.krono.stayfit.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.krono.stayfit.R
import com.krono.stayfit.presentation.theme.StayFitTheme
import com.krono.stayfit.presentation.theme.SubtitleTextColor

sealed class Screen(val route: String) {
    object Home: Screen(route = "home_screen")
    object Steps: Screen(route = "steps_screen")
    //object Heart: Screen(route = "heart_screen")
}

@Composable
fun HomeScreen(navController: NavController) {
    StayFitTheme {

        val listState = rememberScalingLazyListState()

        Scaffold(
            timeText = {
                if(!listState.isScrollInProgress) {
                    TimeText()
                }
                //TimeText(modifier = Modifier.scrollAway(listState))
            },
            vignette = {
                // Only show a Vignette for scrollable screens. This code lab only has one screen,
                // which is scrollable, so we show it all the time.
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            },
            positionIndicator = {
                PositionIndicator(
                    scalingLazyListState = listState
                )
            }
        ) {

            // Modifiers used by our Wear composable.
            val contentModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
            val iconModifier = Modifier
                .size(24.dp)
                .wrapContentSize(align = Alignment.Center)


            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                autoCentering = AutoCenteringParams(itemIndex = 0),
//                contentPadding = PaddingValues(
//                    top = 32.dp,
//                    start = 8.dp,
//                    end = 8.dp,
//                    bottom = 32.dp
//                ),
//                verticalArrangement = Arrangement.Center,
                state = listState
            ) {
                item {
                    ListHeader {
                        Text(text = "Stay Fit")
                    }
                }

                item { StepsTakenChip(navController, contentModifier, iconModifier)}
                item { HeartRateChip(contentModifier, iconModifier) }
                item { BloodOxygenChip(contentModifier, iconModifier) }
            }

        }
    }
}

@Composable
fun StepCounterScreen() {

    //context
    //val context = LocalContext.current
    //scope
    //val scope = rememberCoroutineScope()
    //dataStore for passive data
    //TODO changed passive data initialization
    lateinit var passiveDataRepository: PassiveDataRepository

    //var latestHeartRate by rememberSaveable{ mutableStateOf(0.0) }
    val latestHeartRate = passiveDataRepository.getLatestHeartRateFlow.collectAsState(initial = 0.0)

    StayFitTheme {

        //var latestHeartRate by rememberSaveable{ mutableListOf<>()}
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Spacer(modifier = Modifier.height(height = 18.dp))
//            Text(
//                text = "Steps",
//                style = TextStyle(
//                    fontSize = 18.sp,
//                    fontWeight = Bold
//                )
//            )

            Spacer(modifier = Modifier.height(height = 18.dp))

            Image(
                painter = painterResource(R.drawable.ic_icon_shoes_footprints),
                contentDescription = "Footprint image",
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(height = 25.dp))

            Text(
                text = latestHeartRate.value.toString(),
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = Bold
                ),
                //color = MaterialTheme.colors.primary
            )

            Text(
                text = "Steps",
                style = TextStyle(
                    fontSize = 26.sp,
                    color = SubtitleTextColor
                ),
            )


        }
    }


}

@Preview(
    widthDp = WEAR_PREVIEW_DEVICE_WIDTH_DP,
    heightDp = WEAR_PREVIEW_DEVICE_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun HomeScreenPreview(){
    HomeScreen(navController = rememberSwipeDismissableNavController())
}


//@Preview(
//    widthDp = WEAR_PREVIEW_DEVICE_WIDTH_DP,
//    heightDp = WEAR_PREVIEW_DEVICE_HEIGHT_DP,
//    apiLevel = WEAR_PREVIEW_API_LEVEL,
//    uiMode = WEAR_PREVIEW_UI_MODE,
//    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
//    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
//)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun StepCounterScreenPreview(){
    StayFitTheme {
        //TODO: change paramenter in stepCounter preview
        StepCounterScreen()
    }
}