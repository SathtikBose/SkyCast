package com.buildstack.skycast.data.remote.api

import com.buildstack.skycast.data.remote.dto.AirPollutionDto
import com.buildstack.skycast.data.remote.dto.ForecastDto
import com.buildstack.skycast.data.remote.dto.GeocodingDto
import com.buildstack.skycast.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = com.buildstack.skycast.core.constants.Constants.API_KEY
    ): WeatherDto

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = com.buildstack.skycast.core.constants.Constants.API_KEY
    ): ForecastDto

    @GET("data/2.5/air_pollution")
    suspend fun getCurrentAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = com.buildstack.skycast.core.constants.Constants.API_KEY
    ): AirPollutionDto

    @GET("geo/1.0/direct")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = com.buildstack.skycast.core.constants.Constants.API_KEY
    ): List<GeocodingDto>

    @GET("geo/1.0/reverse")
    suspend fun getReverseGeocoding(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = com.buildstack.skycast.core.constants.Constants.API_KEY
    ): List<GeocodingDto>
}
