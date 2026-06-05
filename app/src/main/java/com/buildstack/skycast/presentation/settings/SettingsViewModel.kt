package com.buildstack.skycast.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("skycast_prefs", Context.MODE_PRIVATE)

    private val _isMetric = MutableStateFlow(prefs.getBoolean("is_metric", true))
    val isMetric: StateFlow<Boolean> = _isMetric.asStateFlow()

    fun toggleUnit(isMetric: Boolean) {
        prefs.edit().putBoolean("is_metric", isMetric).apply()
        _isMetric.value = isMetric
    }
}
