package com.buildstack.skycast.di

import android.content.Context
import androidx.room.Room
import com.buildstack.skycast.core.constants.Constants
import com.buildstack.skycast.data.local.dao.LocationDao
import com.buildstack.skycast.data.local.dao.WeatherDao
import com.buildstack.skycast.data.local.database.SkyCastDatabase
import com.buildstack.skycast.data.remote.api.OpenWeatherApi
import com.buildstack.skycast.data.repository.WeatherRepositoryImpl
import com.buildstack.skycast.domain.repository.WeatherRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenWeatherApi(okHttpClient: OkHttpClient): OpenWeatherApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSkyCastDatabase(@ApplicationContext context: Context): SkyCastDatabase {
        return Room.databaseBuilder(
            context,
            SkyCastDatabase::class.java,
            "skycast_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(db: SkyCastDatabase): WeatherDao = db.weatherDao

    @Provides
    @Singleton
    fun provideLocationDao(db: SkyCastDatabase): LocationDao = db.locationDao

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: OpenWeatherApi,
        dao: WeatherDao,
        gson: Gson
    ): WeatherRepository {
        return WeatherRepositoryImpl(api, dao, gson)
    }
}
