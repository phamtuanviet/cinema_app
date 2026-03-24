package com.example.myapplication.presentation.screen.cinema.cinema_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CinemaListViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CinemaListState())
    val state: StateFlow<CinemaListState> = _state

    fun updateLocation(lat: Double, lng: Double) {
        _state.update {
            it.copy(lat = lat, lng = lng)
        }
    }


    fun loadData(lat: Double, lng: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            coroutineScope {
                val nearbyDeferred = async { repository.getNearby(lat, lng, 100.0) }
                val regionsDeferred = async { repository.getRegions() }

                val nearbyResult = nearbyDeferred.await()
                val regionsResult = regionsDeferred.await()

                val nearby = nearbyResult.getOrNull()
                val regions = regionsResult.getOrNull()

                val error = nearbyResult.exceptionOrNull()
                    ?: regionsResult.exceptionOrNull()
                Log.d("CinemaListViewModel", "nearby: $nearby")


                _state.update {
                    it.copy(
                        isLoading = false,
                        nearbyCinemas = nearby ?: emptyList(),
                        regions = regions ?: emptyList(),
                        error = error?.message
                    )
                }
            }
        }
    }

    fun onRegionClick(region: String, lat: Double, lng: Double) {
        val current = _state.value

        // nếu đã mở thì đóng lại
        if (current.expandedRegion == region) {
            _state.update { it.copy(expandedRegion = null) }
            return
        }

        // nếu đã có data rồi thì chỉ mở
        if (current.cinemasByRegion.containsKey(region)) {
            _state.update { it.copy(expandedRegion = region) }
            return
        }

        // chưa có thì gọi API
        viewModelScope.launch {
            val result = repository.getCinemaByRegion(region, lat, lng)

            result.onSuccess { cinemas ->
                _state.update {
                    it.copy(
                        expandedRegion = region,
                        cinemasByRegion = it.cinemasByRegion + (region to cinemas)
                    )
                }
            }.onFailure {
                _state.update { it.copy(error = "Loading region failed") }
            }
        }
    }
}