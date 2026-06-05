package com.buildstack.skycast.data.remote.dto

import com.buildstack.skycast.domain.model.AirQuality
import com.buildstack.skycast.domain.model.Forecast
import com.buildstack.skycast.domain.model.ForecastItem
import com.buildstack.skycast.domain.model.Location
import com.buildstack.skycast.domain.model.Weather

fun WeatherDto.toWeather(): Weather {
    return Weather(
        temperature = main?.temp ?: 0.0,
        feelsLike = main?.feelsLike ?: 0.0,
        tempMin = main?.tempMin ?: 0.0,
        tempMax = main?.tempMax ?: 0.0,
        humidity = main?.humidity ?: 0,
        pressure = main?.pressure ?: 0,
        windSpeed = wind?.speed ?: 0.0,
        windDirection = wind?.deg ?: 0,
        visibility = visibility ?: 0,
        cloudCoverage = clouds?.all ?: 0,
        condition = weather?.firstOrNull()?.main ?: "",
        description = weather?.firstOrNull()?.description ?: "",
        iconId = weather?.firstOrNull()?.icon ?: "",
        sunrise = sys?.sunrise ?: 0L,
        sunset = sys?.sunset ?: 0L,
        cityName = name ?: "",
        timestamp = dt ?: 0L
    )
}

fun ForecastDto.toForecast(): Forecast {
    return Forecast(
        items = list?.map {
            ForecastItem(
                timestamp = it.dt ?: 0L,
                temperature = it.main?.temp ?: 0.0,
                condition = it.weather?.firstOrNull()?.main ?: "",
                description = it.weather?.firstOrNull()?.description ?: "",
                iconId = it.weather?.firstOrNull()?.icon ?: ""
            )
        } ?: emptyList()
    )
}

fun AirPollutionDto.toAirQuality(): AirQuality? {
    val current = list?.firstOrNull() ?: return null
    return AirQuality(
        aqi = current.main?.aqi ?: 0,
        pm25 = current.components?.pm25 ?: 0.0,
        pm10 = current.components?.pm10 ?: 0.0,
        no2 = current.components?.no2 ?: 0.0,
        o3 = current.components?.o3 ?: 0.0,
        so2 = current.components?.so2 ?: 0.0,
        co = current.components?.co ?: 0.0,
        timestamp = current.dt ?: 0L
    )
}

fun GeocodingDto.toLocation(): Location {
    return Location(
        name = name ?: "",
        lat = lat ?: 0.0,
        lon = lon ?: 0.0,
        country = country ?: "",
        state = state
    )
}
