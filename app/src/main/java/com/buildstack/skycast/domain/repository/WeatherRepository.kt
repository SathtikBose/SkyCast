package com.buildstack.skycast.domain.repository

import com.buildstack.skycast.core.utils.Resource
import com.buildstack.skycast.domain.model.AirQuality
import com.buildstack.skycast.domain.model.Forecast
import com.buildstack.skycast.domain.model.Location
import com.buildstack.skycast.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double, fetchFromRemote: Boolean): Flow<Resource<Weather>>
    fun getForecast(lat: Double, lon: Double, fetchFromRemote: Boolean): Flow<Resource<Forecast>>
    fun getCurrentAirQuality(lat: Double, lon: Double, fetchFromRemote: Boolean): Flow<Resource<AirQuality>>
    suspend fun searchLocation(query: String): Resource<List<Location>>
    suspend fun getReverseGeocoding(lat: Double, lon: Double): Resource<Location>
}
