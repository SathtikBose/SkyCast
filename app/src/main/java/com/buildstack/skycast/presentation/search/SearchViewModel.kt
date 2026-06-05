package com.buildstack.skycast.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildstack.skycast.core.utils.Resource
import com.buildstack.skycast.domain.model.Location
import com.buildstack.skycast.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<Location> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        
        searchJob?.cancel()
        if (query.length > 2) {
            searchJob = viewModelScope.launch {
                delay(500) // Debounce
                _uiState.value = _uiState.value.copy(isSearching = true, error = null)
                when (val response = repository.searchLocation(query)) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isSearching = false,
                            results = response.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isSearching = false,
                            error = response.message,
                            results = emptyList()
                        )
                    }
                    is Resource.Loading -> { } // Already handled
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(results = emptyList(), isSearching = false)
        }
    }
}
