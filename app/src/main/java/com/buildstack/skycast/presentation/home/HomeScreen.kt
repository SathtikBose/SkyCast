package com.buildstack.skycast.presentation.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.buildstack.skycast.domain.model.ForecastItem
import com.buildstack.skycast.domain.model.Weather
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun HomeScreen(
    selectedLat: Double? = null,
    selectedLon: Double? = null,
    onLocationConsumed: () -> Unit = {},
    onSearchClick: () -> Unit,
    onAqiClick: (Double?, Double?) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchCurrentLocationWeather()
        } else {
            // Default to NY if denied
            viewModel.loadWeatherData(40.7128, -74.0060)
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            viewModel.fetchCurrentLocationWeather()
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    LaunchedEffect(selectedLat, selectedLon) {
        if (selectedLat != null && selectedLon != null) {
            viewModel.loadWeatherData(selectedLat, selectedLon, forceRefresh = true)
            onLocationConsumed()
        }
    }

    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    androidx.compose.material3.pulltorefresh.PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AnimatedFogBackground(weather = state.weather)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onSearchClick) {
                        Text("🔍", fontSize = 24.sp)
                    }
                    Row {
                        IconButton(onClick = { onAqiClick(state.lat, state.lon) }) {
                            Text("🍃", fontSize = 24.sp)
                        }
                        IconButton(onClick = onSettingsClick) {
                            Text("⚙️", fontSize = 24.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (state.isLoading && state.weather == null) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    state.weather?.let { weather ->
                        HeroWeatherCard(weather = weather)
                        Spacer(modifier = Modifier.height(24.dp))
                        WeatherDetailsGrid(weather = weather)
                    }

                    state.forecast?.let { forecast ->
                        Spacer(modifier = Modifier.height(24.dp))
                        ForecastCarousel(items = forecast.items)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun AnimatedFogBackground(weather: Weather?) {
    val infiniteTransition = rememberInfiniteTransition(label = "fog_transition")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fog_offset"
    )

    val colorStart = Color(0xFF1E1E1E).copy(alpha = 0.4f)
    val colorEnd = Color(0xFF000000).copy(alpha = 0.8f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(colorStart, colorEnd),
                    start = Offset(0f, offsetY),
                    end = Offset(1000f, offsetY + 1000f)
                )
            )
    )
}

@Composable
fun HeroWeatherCard(weather: Weather) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C3E50),
                        Color(0xFF000000)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = weather.cityName,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium
            )

            // Placeholder for 3D Weather Illustration
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(60.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌧️", fontSize = 64.sp) // Replace with real asset later
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${weather.temperature.roundToInt()}°",
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weather.description.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    color = Color.Gray,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun WeatherDetailsGrid(weather: Weather) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(modifier = Modifier.weight(1f), title = "Feels Like", value = "${weather.feelsLike.roundToInt()}°")
            DetailCard(modifier = Modifier.weight(1f), title = "Humidity", value = "${weather.humidity}%")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(modifier = Modifier.weight(1f), title = "Wind", value = "${weather.windSpeed} m/s")
            DetailCard(modifier = Modifier.weight(1f), title = "Visibility", value = "${weather.visibility / 1000} km")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(modifier = Modifier.weight(1f), title = "Pressure", value = "${weather.pressure} hPa")
            DetailCard(modifier = Modifier.weight(1f), title = "Clouds", value = "${weather.cloudCoverage}%")
        }
    }
}

@Composable
fun DetailCard(modifier: Modifier = Modifier, title: String, value: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ForecastCarousel(items: List<ForecastItem>) {
    Column {
        Text(
            text = "Hourly Forecast",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                ForecastItemCard(item)
            }
        }
    }
}

@Composable
fun ForecastItemCard(item: ForecastItem) {
    val date = Date(item.timestamp * 1000)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

    Box(
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E1E1E))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = timeFormat, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "☁️", fontSize = 24.sp) // Replace with real asset
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${item.temperature.roundToInt()}°", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
