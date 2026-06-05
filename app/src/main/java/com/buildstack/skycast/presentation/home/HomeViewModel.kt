package com.buildstack.skycast.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildstack.skycast.core.utils.LocationProvider
import com.buildstack.skycast.core.utils.Resource
import com.buildstack.skycast.domain.model.AirQuality
import com.buildstack.skycast.domain.model.Forecast
import com.buildstack.skycast.domain.model.Weather
import com.buildstack.skycast.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val forecast: Forecast? = null,
    val airQuality: AirQuality? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun fetchCurrentLocationWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                loadWeatherData(location.latitude, location.longitude)
            } else {
                // Fallback to a default or show error
                _uiState.value = _uiState.value.copy(error = "Could not get location", isLoading = false)
            }
        }
    }

    fun refresh() {
        val currentLat = _uiState.value.lat
        val currentLon = _uiState.value.lon
        if (currentLat != null && currentLon != null) {
            loadWeatherData(currentLat, currentLon, forceRefresh = true)
        } else {
            fetchCurrentLocationWeather()
        }
    }

    fun loadWeatherData(lat: Double, lon: Double, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            combine(
                repository.getCurrentWeather(lat, lon, forceRefresh),
                repository.getForecast(lat, lon, forceRefresh),
                repository.getCurrentAirQuality(lat, lon, forceRefresh)
            ) { weatherRes, forecastRes, aqiRes ->
                val isLoading = weatherRes is Resource.Loading || forecastRes is Resource.Loading || aqiRes is Resource.Loading
                
                val error = listOf(weatherRes, forecastRes, aqiRes)
                    .filterIsInstance<Resource.Error<*>>()
                    .firstOrNull()?.message

                HomeUiState(
                    isLoading = isLoading,
                    weather = weatherRes.data ?: _uiState.value.weather,
                    forecast = forecastRes.data ?: _uiState.value.forecast,
                    airQuality = aqiRes.data ?: _uiState.value.airQuality,
                    lat = lat,
                    lon = lon,
                    error = error
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
