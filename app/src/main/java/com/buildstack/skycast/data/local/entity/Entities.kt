package com.buildstack.skycast.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val searchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val id: String, // e.g. "lat_lon"
    val cityName: String,
    val weatherJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "forecast_cache")
data class ForecastCacheEntity(
    @PrimaryKey val id: String,
    val cityName: String,
    val forecastJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "aqi_cache")
data class AqiCacheEntity(
    @PrimaryKey val id: String,
    val cityName: String,
    val aqiJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)
