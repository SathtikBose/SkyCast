package com.buildstack.skycast.presentation.airquality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.buildstack.skycast.domain.model.AirQuality

import androidx.compose.runtime.LaunchedEffect

@Composable
fun AqiScreen(
    lat: Double,
    lon: Double,
    onBackClick: () -> Unit,
    viewModel: AqiViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lat, lon) {
        viewModel.loadAqiData(lat, lon)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Text("←", color = Color.White, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Air Quality Index", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            state.airQuality?.let { aqi ->
                val (aqiText, aqiColor) = getAqiLevel(aqi.aqi)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(aqiColor.copy(alpha = 0.2f))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = aqi.aqi.toString(),
                            color = aqiColor,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = aqiText,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Pollutants", color = Color.Gray, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PollutantCard(modifier = Modifier.weight(1f), name = "PM2.5", value = aqi.pm25.toString())
                    PollutantCard(modifier = Modifier.weight(1f), name = "PM10", value = aqi.pm10.toString())
                    PollutantCard(modifier = Modifier.weight(1f), name = "NO2", value = aqi.no2.toString())
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PollutantCard(modifier = Modifier.weight(1f), name = "O3", value = aqi.o3.toString())
                    PollutantCard(modifier = Modifier.weight(1f), name = "SO2", value = aqi.so2.toString())
                    PollutantCard(modifier = Modifier.weight(1f), name = "CO", value = aqi.co.toString())
                }
            }
        }
    }
}

@Composable
fun PollutantCard(modifier: Modifier = Modifier, name: String, value: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        Column {
            Text(text = name, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

fun getAqiLevel(aqi: Int): Pair<String, Color> {
    return when (aqi) {
        1 -> "Good" to Color(0xFF00E400)
        2 -> "Fair" to Color(0xFFFFFF00)
        3 -> "Moderate" to Color(0xFFFF7E00)
        4 -> "Poor" to Color(0xFFFF0000)
        5 -> "Very Poor" to Color(0xFF8F3F97)
        else -> "Unknown" to Color.Gray
    }
}
