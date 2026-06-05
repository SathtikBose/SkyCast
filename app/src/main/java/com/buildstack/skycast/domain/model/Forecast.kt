package com.buildstack.skycast.domain.model

data class Forecast(
    val items: List<ForecastItem>
)

data class ForecastItem(
    val timestamp: Long,
    val temperature: Double,
    val condition: String,
    val description: String,
    val iconId: String
)
