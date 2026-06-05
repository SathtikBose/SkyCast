package com.buildstack.skycast.domain.model

data class AirQuality(
    val aqi: Int,
    val pm25: Double,
    val pm10: Double,
    val no2: Double,
    val o3: Double,
    val so2: Double,
    val co: Double,
    val timestamp: Long
)
