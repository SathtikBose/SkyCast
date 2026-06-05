package com.buildstack.skycast.presentation.airquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildstack.skycast.core.utils.Resource
import com.buildstack.skycast.domain.model.AirQuality
import com.buildstack.skycast.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AqiUiState(
    val isLoading: Boolean = false,
    val airQuality: AirQuality? = null,
    val error: String? = null
)

@HiltViewModel
class AqiViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AqiUiState())
    val uiState: StateFlow<AqiUiState> = _uiState.asStateFlow()

    fun loadAqiData(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getCurrentAirQuality(lat, lon, false).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = AqiUiState(airQuality = result.data)
                    }
                    is Resource.Error -> {
                        _uiState.value = AqiUiState(error = result.message, airQuality = result.data)
                    }
                    is Resource.Loading -> {
                        _uiState.value = AqiUiState(isLoading = true, airQuality = result.data)
                    }
                }
            }
        }
    }
}
