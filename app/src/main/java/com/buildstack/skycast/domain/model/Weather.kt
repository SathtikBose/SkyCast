package com.buildstack.skycast.domain.model

data class Weather(
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val visibility: Int,
    val cloudCoverage: Int,
    val condition: String,
    val description: String,
    val iconId: String,
    val sunrise: Long,
    val sunset: Long,
    val cityName: String,
    val timestamp: Long
)
