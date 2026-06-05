package com.buildstack.skycast.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.buildstack.skycast.data.local.dao.LocationDao
import com.buildstack.skycast.data.local.dao.WeatherDao
import com.buildstack.skycast.data.local.entity.AqiCacheEntity
import com.buildstack.skycast.data.local.entity.FavoriteCityEntity
import com.buildstack.skycast.data.local.entity.ForecastCacheEntity
import com.buildstack.skycast.data.local.entity.RecentSearchEntity
import com.buildstack.skycast.data.local.entity.WeatherCacheEntity

@Database(
    entities = [
        FavoriteCityEntity::class,
        RecentSearchEntity::class,
        WeatherCacheEntity::class,
        ForecastCacheEntity::class,
        AqiCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SkyCastDatabase : RoomDatabase() {
    abstract val weatherDao: WeatherDao
    abstract val locationDao: LocationDao
}
