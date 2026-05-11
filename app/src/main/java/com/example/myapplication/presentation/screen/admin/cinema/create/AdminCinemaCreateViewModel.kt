package com.example.myapplication.presentation.screen.admin.cinema.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminCinemaCreateRequest
import com.example.myapplication.domain.repository.AdminCinemaRepository
import com.example.myapplication.presentation.screen.admin.cinema.edit.CinemaEditEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminCinemaCreateState(
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val region: String = "",
    val cineplex: String = "",
    val latitude: String = "", // Để String cho TextField, khi gửi sẽ convert sang Double
    val longitude: String = "",
    val googleMapsLink: String = "",
    val isActive: Boolean = true,
    val logoUri: Uri? = null,

    val availableRegions: List<String> = emptyList(),
    val availableCineplexes: List<String> = emptyList(),

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

sealed class CinemaCreateEvent {
    data class NameChanged(val name: String) : CinemaCreateEvent()
    data class AddressChanged(val address: String) : CinemaCreateEvent()
    data class DescriptionChanged(val desc: String) : CinemaCreateEvent()
    data class RegionChanged(val region: String) : CinemaCreateEvent()
    data class CineplexChanged(val cineplex: String) : CinemaCreateEvent()
    data class LatitudeChanged(val lat: String) : CinemaCreateEvent()
    data class LongitudeChanged(val lng: String) : CinemaCreateEvent()
    data class IsActiveChanged(val isActive: Boolean) : CinemaCreateEvent()
    data class LogoPicked(val uri: Uri?) : CinemaCreateEvent()

    data class GoogleMapsLinkChanged(val link: String) : CinemaCreateEvent()

}

@HiltViewModel
class AdminCinemaCreateViewModel @Inject constructor(
    private val repository: AdminCinemaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminCinemaCreateState())
    val state = _state.asStateFlow()

    init {
        loadSuggestions()
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            val regionsDeferred = async { repository.getRegions() }
            val cineplexesDeferred = async { repository.getCineplexes() }

            val regionsResult = regionsDeferred.await()
            val cineplexesResult = cineplexesDeferred.await()

            _state.update {
                it.copy(
                    availableRegions = regionsResult.getOrNull().orEmpty(),
                    availableCineplexes = cineplexesResult.getOrNull().orEmpty()
                )
            }
        }
    }

    fun onEvent(event: CinemaCreateEvent) {
        when (event) {
            is CinemaCreateEvent.NameChanged -> _state.update { it.copy(name = event.name, error = null) }
            is CinemaCreateEvent.AddressChanged -> _state.update { it.copy(address = event.address) }
            is CinemaCreateEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is CinemaCreateEvent.RegionChanged -> _state.update { it.copy(region = event.region) }
            is CinemaCreateEvent.CineplexChanged -> _state.update { it.copy(cineplex = event.cineplex) }
            is CinemaCreateEvent.LatitudeChanged -> _state.update { it.copy(latitude = event.lat) }
            is CinemaCreateEvent.LongitudeChanged -> _state.update { it.copy(longitude = event.lng) }
            is CinemaCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is CinemaCreateEvent.LogoPicked -> _state.update { it.copy(logoUri = event.uri) }
            is CinemaCreateEvent.GoogleMapsLinkChanged -> {
                _state.update { it.copy(googleMapsLink = event.link) }
                extractCoordinatesFromLink(event.link)
            }
        }
    }

    private fun extractCoordinatesFromLink(link: String) {
        // Regex tìm đoạn bắt đầu bằng '@', theo sau là 2 số thập phân (có thể có dấu trừ) cách nhau bởi dấu phẩy
        val regex = """@(-?\d+\.\d+),(-?\d+\.\d+)""".toRegex()
        val matchResult = regex.find(link)

        if (matchResult != null) {
            // Lấy ra đúng 2 giá trị đã match
            val (lat, lng) = matchResult.destructured
            _state.update {
                it.copy(
                    latitude = lat,
                    longitude = lng,
                    error = null // Xoá lỗi nếu có
                )
            }
        }
    }

    fun createCinema(context: Context) {
        val currentState = _state.value

        // Validate cơ bản
        if (currentState.name.isBlank() || currentState.address.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập Tên Rạp và Địa chỉ") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val request = AdminCinemaCreateRequest(
                name = currentState.name.trim(),
                address = currentState.address.trim(),
                description = currentState.description.trim().takeIf { it.isNotEmpty() },
                region = currentState.region.trim().takeIf { it.isNotEmpty() },
                cineplex = currentState.cineplex.trim().takeIf { it.isNotEmpty() },
                latitude = currentState.latitude.toDoubleOrNull(),
                longitude = currentState.longitude.toDoubleOrNull(),
                isActive = currentState.isActive
            )

            val result = repository.createCinema(request, currentState.logoUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}