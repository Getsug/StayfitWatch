package com.krono.stayfit.presentation

import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PassiveDataRepository,
    private val healthServicesManager: HealthServicesManager

): ViewModel() {

    private val context = LocalContext

    private val _uiState = MutableStateFlow<UiState>(UiState.Startup)
    val uiState: StateFlow<UiState> = _uiState


    val passiveDataEnabled: Flow<Boolean>
    val latestHeartRate = repository.getLatestHeartRateFlow
    val latestStepsDaily = repository.getLatestStepsDailyFlow


    //TODO: handle availability and lack of capabilities for HEART_RATE or STEPS_DAILY
    init {
        viewModelScope.launch {
            _uiState.value = if (healthServicesManager.supportsHeartRate()) {
                UiState.HasHeartRateSupport
            } else {
                UiState.NoHeartRateSupport
            }
        }


        //TODO: find a way enable passive data  from the UI
        passiveDataEnabled = repository.getPassiveDataEnabledFlow
            .distinctUntilChanged()
            .onEach { enabled ->
                viewModelScope.launch {
                    if (enabled) {
//                        healthServicesManager.registerForHeartRateData()
//                        healthServicesManager.registerForStepsDaily()
                        healthServicesManager.registerPassiveData()
                    }
                    else{
//                        healthServicesManager.unregisterForHeartRateData()
//                        healthServicesManager.unregisterForStepsDaily()
                        healthServicesManager.unregisterForPassiveData()
                    }
                }
            }
    }

    fun togglePassiveData(enabled: Boolean) {
        viewModelScope.launch {
            repository.setPassiveDataEnabled(enabled)
        }
    }
}

sealed class UiState {
    object Startup: UiState()
    object HasHeartRateSupport: UiState()
    object NoHeartRateSupport: UiState()
}