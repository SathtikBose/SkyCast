package com.buildstack.skycast.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.buildstack.skycast.data.local.entity.AqiCacheEntity
import com.buildstack.skycast.data.local.entity.FavoriteCityEntity
import com.buildstack.skycast.data.local.entity.ForecastCacheEntity
import com.buildstack.skycast.data.local.entity.RecentSearchEntity
import com.buildstack.skycast.data.local.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCache(cache: WeatherCacheEntity)

    @Query("SELECT * FROM weather_cache WHERE id = :id")
    suspend fun getWeatherCache(id: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastCache(cache: ForecastCacheEntity)

    @Query("SELECT * FROM forecast_cache WHERE id = :id")
    suspend fun getForecastCache(id: String): ForecastCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAqiCache(cache: AqiCacheEntity)

    @Query("SELECT * FROM aqi_cache WHERE id = :id")
    suspend fun getAqiCache(id: String): AqiCacheEntity?
}

@Dao
interface LocationDao {
    @Query("SELECT * FROM favorite_cities ORDER BY createdAt DESC")
    fun getFavoriteCities(): Flow<List<FavoriteCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCity(city: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE id = :id")
    suspend fun deleteFavoriteCity(id: Int)

    @Query("SELECT * FROM recent_searches ORDER BY searchedAt DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(search: RecentSearchEntity)
}
