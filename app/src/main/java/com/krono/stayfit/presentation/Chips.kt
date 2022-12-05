package com.krono.stayfit.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.krono.stayfit.R
import com.krono.stayfit.presentation.theme.StayFitTheme




@Composable
fun StepsTakenChip(
    navController: NavController,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {

    Chip(
        modifier = modifier,
        onClick = {
                  //Navigate to StepCounter Screen
                  navController.navigate(Screen.Steps.route)
        },
        label = {
            Text(
                text = "Steps Taken",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_icon_footprint),
                contentDescription = "checking number of steps",
                modifier = iconModifier
            )
        },
    )
}

@Composable
fun HeartRateChip(
    navController: NavController,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
) {
    Chip(
        modifier = modifier,
        onClick = {
            //Navigate to HearRate Screen
            navController.navigate(Screen.Heart.route)
        },
        label = {
            Text(
                text = "Heart Rate",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_icon_heart_rate),
                contentDescription = "checking Heart Rate",
                modifier = iconModifier
            )
        },
    )
}

@Composable
fun BloodOxygenChip(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
) {
    Chip(
        modifier = modifier,
        onClick = { /* ... */ },
        label = {
            Text(
                text = "Blood Oxygen Saturation",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_icon_blood_oxygen),
                contentDescription = "checking blood oxygen",
                modifier = iconModifier
            )
        },
    )
}

@Composable
fun SettingsChip(
    navController: NavController,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {

    Chip(
        modifier = modifier,
        onClick = {
            //Navigate to StepCounter Screen
            navController.navigate(Screen.Settings.route)
        },
        label = {
            Text(
                text = "Settings",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_icon_settings),
                contentDescription = "Go to settings page",
                modifier = iconModifier
            )
        },
    )
}


//TODO: needs checking
@Composable
fun PassiveDataEnabledChip(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    passiveDataEnabled: Boolean,
//fun PassiveDataEnabledChip(
//    modifier: Modifier = Modifier,
//    passiveDataEnabled: Boolean,
//    iconModifier: Modifier = Modifier

) {
    //val myModel: MainViewModel = viewModel()
    //TODO: Onchange, save the value  of toggle switch to the dataStore repository
    //var checked by rememberSaveable { mutableStateOf(passiveDataEnabled)}
    //val passive = viewModel.passiveDataEnabled.collectAsState(initial = false)
    //var checked by rememberSaveable { mutableStateOf(passive.value)}
    var checked = passiveDataEnabled


    ToggleChip(
        modifier = modifier,
        checked = checked,
        onCheckedChange = {checked = it},
        enabled = true,
        label = {
            Text(
                text ="Passive Data",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        },

        toggleControl = {
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    Log.d(PASSIVE_DATA_TAG, "Changed value of passive data state")
                    viewModel.togglePassiveData(checked)
                },
                modifier = Modifier.semantics {
                    this.contentDescription = if (checked) "Enabled" else "Disabled"
                }

            )

        }
    )



}



@Preview(
    group = "Chip",
    widthDp = WEAR_PREVIEW_ROW_WIDTH_DP,
    heightDp = WEAR_PREVIEW_ROW_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun StepsTakenChipPreview(navController: NavController = rememberSwipeDismissableNavController()) {
    StayFitTheme {
        StepsTakenChip(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            iconModifier = Modifier
                .size(24.dp)
                .wrapContentSize(align = Alignment.Center)
        )
    }
}

@Preview(
    group = "Chip",
    widthDp = WEAR_PREVIEW_ROW_WIDTH_DP,
    heightDp = WEAR_PREVIEW_ROW_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun HeartRatePreview(navController: NavController = rememberSwipeDismissableNavController()) {
    StayFitTheme {
        HeartRateChip(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            iconModifier = Modifier
                .size(24.dp)
                .wrapContentSize(align = Alignment.Center)
        )
    }
}

@Preview(
    group = "Chip",
    widthDp = WEAR_PREVIEW_ROW_WIDTH_DP,
    heightDp = WEAR_PREVIEW_ROW_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun BloodOxygenPreview() {
    StayFitTheme {
        BloodOxygenChip(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            iconModifier = Modifier
                .size(24.dp)
                .wrapContentSize(align = Alignment.Center)
        )
    }
}

@Preview(
    group = "Chip",
    widthDp = WEAR_PREVIEW_ROW_WIDTH_DP,
    heightDp = WEAR_PREVIEW_ROW_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun SettingsPreview(navController: NavController = rememberSwipeDismissableNavController()) {
    StayFitTheme {
        SettingsChip(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            iconModifier = Modifier
                .size(24.dp)
                .wrapContentSize(align = Alignment.Center)
        )
    }
}


@Preview(
    group = "Chip",
    widthDp = WEAR_PREVIEW_ROW_WIDTH_DP,
    heightDp = WEAR_PREVIEW_ROW_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun PassiveDataEnabledPreview(passiveDataEnabled: Boolean = true) {
    val viewModel: MainViewModel = viewModel()
    StayFitTheme {
        PassiveDataEnabledChip(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            viewModel = viewModel,
            passiveDataEnabled
            //passiveDataEnabled
//            iconModifier = Modifier
//                .size(24.dp)
//                .wrapContentSize(align = Alignment.Center)
        )
    }
}