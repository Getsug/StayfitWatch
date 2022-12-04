package com.krono.stayfit.presentation

import androidx.compose.runtime.collectAsState
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

    private val _uiState = MutableStateFlow<UiState>(UiState.Startup)
    val uiState: StateFlow<UiState> = _uiState

    val passiveDataEnabled: Flow<Boolean>
    val latestHeartRate = repository.getLatestHeartRateFlow

    init {
        viewModelScope.launch {
            _uiState.value = if (healthServicesManager.supportsHeartRate()) {
                UiState.HasHeartRateSupport
            } else {
                UiState.NoHeartRateSupport
            }
        }


        passiveDataEnabled = repository.getPassiveDataEnabledFlow
            .distinctUntilChanged()
            .onEach { enabled ->
                viewModelScope.launch {
                    if (enabled)
                        healthServicesManager.registerForHeartRateData()
                    else
                        healthServicesManager.unregisterForHeartRateData()
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