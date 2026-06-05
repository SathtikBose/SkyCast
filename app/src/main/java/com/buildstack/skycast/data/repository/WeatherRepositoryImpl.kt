package com.buildstack.skycast.data.repository

import com.buildstack.skycast.core.utils.Resource
import com.buildstack.skycast.data.local.dao.WeatherDao
import com.buildstack.skycast.data.local.entity.AqiCacheEntity
import com.buildstack.skycast.data.local.entity.ForecastCacheEntity
import com.buildstack.skycast.data.local.entity.WeatherCacheEntity
import com.buildstack.skycast.data.remote.api.OpenWeatherApi
import com.buildstack.skycast.data.remote.dto.AirPollutionDto
import com.buildstack.skycast.data.remote.dto.ForecastDto
import com.buildstack.skycast.data.remote.dto.WeatherDto
import com.buildstack.skycast.data.remote.dto.toAirQuality
import com.buildstack.skycast.data.remote.dto.toForecast
import com.buildstack.skycast.data.remote.dto.toLocation
import com.buildstack.skycast.data.remote.dto.toWeather
import com.buildstack.skycast.domain.model.AirQuality
import com.buildstack.skycast.domain.model.Forecast
import com.buildstack.skycast.domain.model.Location
import com.buildstack.skycast.domain.model.Weather
import com.buildstack.skycast.domain.repository.WeatherRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val dao: WeatherDao,
    private val gson: Gson
) : WeatherRepository {

    override fun getCurrentWeather(
        lat: Double,
        lon: Double,
        fetchFromRemote: Boolean
    ): Flow<Resource<Weather>> = flow {
        emit(Resource.Loading())
        val cacheId = "${lat}_${lon}"
        
        val cached = dao.getWeatherCache(cacheId)
        if (cached != null && !fetchFromRemote) {
            try {
                val dto = gson.fromJson(cached.weatherJson, WeatherDto::class.java)
                emit(Resource.Success(dto.toWeather()))
                return@flow
            } catch (e: Exception) {
                // Ignore parse error, fetch from remote
            }
        }

        try {
            val remoteWeather = api.getCurrentWeather(lat = lat, lon = lon)
            dao.insertWeatherCache(
                WeatherCacheEntity(
                    id = cacheId,
                    cityName = remoteWeather.name ?: "",
                    weatherJson = gson.toJson(remoteWeather)
                )
            )
            emit(Resource.Success(remoteWeather.toWeather()))
        } catch (e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!", cached?.let { gson.fromJson(it.weatherJson, WeatherDto::class.java).toWeather() }))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server, check your internet connection.", cached?.let { gson.fromJson(it.weatherJson, WeatherDto::class.java).toWeather() }))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown error occurred"))
        }
    }

    override fun getForecast(
        lat: Double,
        lon: Double,
        fetchFromRemote: Boolean
    ): Flow<Resource<Forecast>> = flow {
        emit(Resource.Loading())
        val cacheId = "${lat}_${lon}"
        
        val cached = dao.getForecastCache(cacheId)
        if (cached != null && !fetchFromRemote) {
            try {
                val dto = gson.fromJson(cached.forecastJson, ForecastDto::class.java)
                emit(Resource.Success(dto.toForecast()))
                return@flow
            } catch (e: Exception) {
                // Ignore
            }
        }

        try {
            val remoteForecast = api.getForecast(lat = lat, lon = lon)
            dao.insertForecastCache(
                ForecastCacheEntity(
                    id = cacheId,
                    cityName = remoteForecast.city?.name ?: "",
                    forecastJson = gson.toJson(remoteForecast)
                )
            )
            emit(Resource.Success(remoteForecast.toForecast()))
        } catch (e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!", cached?.let { gson.fromJson(it.forecastJson, ForecastDto::class.java).toForecast() }))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server, check your internet connection.", cached?.let { gson.fromJson(it.forecastJson, ForecastDto::class.java).toForecast() }))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown error occurred"))
        }
    }

    override fun getCurrentAirQuality(
        lat: Double,
        lon: Double,
        fetchFromRemote: Boolean
    ): Flow<Resource<AirQuality>> = flow {
        emit(Resource.Loading())
        val cacheId = "${lat}_${lon}"
        
        val cached = dao.getAqiCache(cacheId)
        if (cached != null && !fetchFromRemote) {
            try {
                val dto = gson.fromJson(cached.aqiJson, AirPollutionDto::class.java)
                dto.toAirQuality()?.let {
                    emit(Resource.Success(it))
                    return@flow
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        try {
            val remoteAqi = api.getCurrentAirPollution(lat = lat, lon = lon)
            dao.insertAqiCache(
                AqiCacheEntity(
                    id = cacheId,
                    cityName = "",
                    aqiJson = gson.toJson(remoteAqi)
                )
            )
            val aqiDomain = remoteAqi.toAirQuality()
            if (aqiDomain != null) {
                emit(Resource.Success(aqiDomain))
            } else {
                emit(Resource.Error("Invalid AQI data"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server, check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown error occurred"))
        }
    }

    override suspend fun searchLocation(query: String): Resource<List<Location>> {
        return try {
            val result = api.searchLocation(query = query)
            Resource.Success(result.map { it.toLocation() })
        } catch (e: HttpException) {
            Resource.Error("Oops, something went wrong!")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server, check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("Unknown error occurred")
        }
    }

    override suspend fun getReverseGeocoding(lat: Double, lon: Double): Resource<Location> {
        return try {
            val result = api.getReverseGeocoding(lat = lat, lon = lon)
            val location = result.firstOrNull()?.toLocation()
            if (location != null) {
                Resource.Success(location)
            } else {
                Resource.Error("Location not found")
            }
        } catch (e: HttpException) {
            Resource.Error("Oops, something went wrong!")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server, check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("Unknown error occurred")
        }
    }
}
