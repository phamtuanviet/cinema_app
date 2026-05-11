package com.example.myapplication.presentation.screen.admin.cinema.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminCinemaUpdateRequest
import com.example.myapplication.domain.repository.AdminCinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


// --- STATE ---
data class AdminCinemaEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    // Form data
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val region: String = "",
    val cineplex: String = "",
    val googleMapsLink: String = "", // Ô nhập link GG Maps
    val latitude: String = "",
    val longitude: String = "",
    val isActive: Boolean = true,

    val currentLogoUrl: String? = null, // Ảnh hiện tại trên server
    val newLogoUri: Uri? = null,        // Ảnh mới người dùng vừa chọn

    val availableRegions: List<String> = emptyList(),
    val availableCineplexes: List<String> = emptyList()
)

// --- EVENT ---
sealed class CinemaEditEvent {
    data class NameChanged(val name: String) : CinemaEditEvent()
    data class AddressChanged(val address: String) : CinemaEditEvent()
    data class DescriptionChanged(val desc: String) : CinemaEditEvent()
    data class RegionChanged(val region: String) : CinemaEditEvent()
    data class CineplexChanged(val cineplex: String) : CinemaEditEvent()
    data class GoogleMapsLinkChanged(val link: String) : CinemaEditEvent()
    data class LatitudeChanged(val lat: String) : CinemaEditEvent()
    data class LongitudeChanged(val lng: String) : CinemaEditEvent()
    data class IsActiveChanged(val isActive: Boolean) : CinemaEditEvent()
    data class LogoPicked(val uri: Uri?) : CinemaEditEvent()
}

// --- VIEWMODEL ---
@HiltViewModel
class AdminCinemaEditViewModel @Inject constructor(
    private val repository: AdminCinemaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cinemaId: String = checkNotNull(savedStateHandle["cinemaId"])

    private val _state = MutableStateFlow(AdminCinemaEditState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true, error = null) }

            // Gọi 3 API cùng lúc để tiết kiệm thời gian
            val cinemaDeferred = async { repository.getCinemaById(cinemaId) }
            val regionsDeferred = async { repository.getRegions() }
            val cineplexesDeferred = async { repository.getCineplexes() }

            val cinemaResult = cinemaDeferred.await()
            val regionsResult = regionsDeferred.await()
            val cineplexesResult = cineplexesDeferred.await()

            if (cinemaResult.isSuccess) {
                val cinema = cinemaResult.getOrNull()!!

                _state.update {
                    it.copy(
                        isLoadingData = false,
                        availableRegions = regionsResult.getOrNull().orEmpty(),
                        availableCineplexes = cineplexesResult.getOrNull().orEmpty(),

                        name = cinema.name,
                        address = cinema.address,
                        description = cinema.description ?: "",
                        region = cinema.region ?: "",
                        cineplex = cinema.cineplex ?: "",
                        latitude = cinema.latitude?.toString() ?: "",
                        longitude = cinema.longitude?.toString() ?: "",
                        isActive = cinema.isActive,
                        currentLogoUrl = cinema.logoUrl
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoadingData = false,
                        error = cinemaResult.exceptionOrNull()?.message ?: "Lỗi tải thông tin rạp"
                    )
                }
            }
        }
    }

    fun onEvent(event: CinemaEditEvent) {
        when (event) {
            is CinemaEditEvent.NameChanged -> _state.update { it.copy(name = event.name, error = null) }
            is CinemaEditEvent.AddressChanged -> _state.update { it.copy(address = event.address) }
            is CinemaEditEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is CinemaEditEvent.RegionChanged -> _state.update { it.copy(region = event.region) }
            is CinemaEditEvent.CineplexChanged -> _state.update { it.copy(cineplex = event.cineplex) }
            is CinemaEditEvent.LatitudeChanged -> _state.update { it.copy(latitude = event.lat) }
            is CinemaEditEvent.LongitudeChanged -> _state.update { it.copy(longitude = event.lng) }
            is CinemaEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is CinemaEditEvent.LogoPicked -> _state.update { it.copy(newLogoUri = event.uri) } // Cập nhật ảnh mới

            // Xử lý dán link Google Maps -> Tự tách Vĩ độ, Kinh độ
            is CinemaEditEvent.GoogleMapsLinkChanged -> {
                _state.update { it.copy(googleMapsLink = event.link) }
                extractCoordinatesFromLink(event.link)
            }
        }
    }

    private fun extractCoordinatesFromLink(link: String) {
        // Regex bóc tách 2 số thập phân sau chữ @
        val regex = """@(-?\d+\.\d+),(-?\d+\.\d+)""".toRegex()
        val matchResult = regex.find(link)

        if (matchResult != null) {
            val (lat, lng) = matchResult.destructured
            _state.update {
                it.copy(
                    latitude = lat,
                    longitude = lng,
                    error = null
                )
            }
        }
    }

    fun updateCinema(context: Context) {
        val currentState = _state.value

        if (currentState.name.isBlank() || currentState.address.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập Tên Rạp và Địa chỉ") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminCinemaUpdateRequest(
                name = currentState.name.trim(),
                address = currentState.address.trim(),
                description = currentState.description.trim().takeIf { it.isNotEmpty() },
                region = currentState.region.trim().takeIf { it.isNotEmpty() },
                cineplex = currentState.cineplex.trim().takeIf { it.isNotEmpty() },
                latitude = currentState.latitude.toDoubleOrNull(),
                longitude = currentState.longitude.toDoubleOrNull(),
                isActive = currentState.isActive
            )

            // Dùng newLogoUri (nếu user chọn ảnh mới), nếu không thì API Retrofit gửi part null, SpringBoot giữ ảnh cũ
            val result = repository.updateCinema(cinemaId, request, currentState.newLogoUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}