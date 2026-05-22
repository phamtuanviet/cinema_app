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
    val nameError: String? = null,

    val address: String = "",
    val addressError: String? = null,

    val description: String = "",
    val region: String = "",
    val cineplex: String = "",

    val googleMapsLink: String = "",

    val latitude: String = "",
    val latError: String? = null,

    val longitude: String = "",
    val lngError: String? = null,

    val isActive: Boolean = true,
    val logoUri: Uri? = null,

    val availableRegions: List<String> = emptyList(),
    val availableCineplexes: List<String> = emptyList(),

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null // Lỗi chung cho Toast (VD: Lỗi mạng, quên chọn ảnh)
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
            // 🔥 Cập nhật Text và XÓA LỖI ngay khi người dùng gõ lại
            is CinemaCreateEvent.NameChanged -> _state.update { it.copy(name = event.name, nameError = null) }
            is CinemaCreateEvent.AddressChanged -> _state.update { it.copy(address = event.address, addressError = null) }
            is CinemaCreateEvent.LatitudeChanged -> _state.update { it.copy(latitude = event.lat, latError = null) }
            is CinemaCreateEvent.LongitudeChanged -> _state.update { it.copy(longitude = event.lng, lngError = null) }

            is CinemaCreateEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is CinemaCreateEvent.RegionChanged -> _state.update { it.copy(region = event.region) }
            is CinemaCreateEvent.CineplexChanged -> _state.update { it.copy(cineplex = event.cineplex) }
            is CinemaCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is CinemaCreateEvent.LogoPicked -> _state.update { it.copy(logoUri = event.uri, error = null) }
            is CinemaCreateEvent.GoogleMapsLinkChanged -> {
                _state.update { it.copy(googleMapsLink = event.link) }
                extractCoordinatesFromLink(event.link)
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun extractCoordinatesFromLink(link: String) {
        val regex = """@(-?\d+\.\d+),(-?\d+\.\d+)""".toRegex()
        val matchResult = regex.find(link)

        if (matchResult != null) {
            val (lat, lng) = matchResult.destructured
            _state.update {
                it.copy(
                    latitude = lat,
                    longitude = lng,
                    latError = null,
                    lngError = null
                )
            }
        }
    }

    fun createCinema(context: Context) {
        val st = _state.value
        var isValid = true

        // 1. Kiểm tra Logo (Dùng error chung để hiện Toast)
        if (st.logoUri == null) {
            _state.update { it.copy(error = "Vui lòng chọn ảnh Logo cho rạp!") }
            return
        }

        // 2. Kiểm tra Tên & Địa chỉ (Báo lỗi đỏ tại ô nhập)
        if (st.name.isBlank()) {
            _state.update { it.copy(nameError = "Tên rạp không được để trống") }
            isValid = false
        }
        if (st.address.isBlank()) {
            _state.update { it.copy(addressError = "Địa chỉ rạp không được để trống") }
            isValid = false
        }

        // 3. Kiểm tra định dạng Toạ độ (nếu có nhập)
        val latDouble = st.latitude.toDoubleOrNull()
        if (st.latitude.isNotBlank() && latDouble == null) {
            _state.update { it.copy(latError = "Vĩ độ không hợp lệ") }
            isValid = false
        }
        val lngDouble = st.longitude.toDoubleOrNull()
        if (st.longitude.isNotBlank() && lngDouble == null) {
            _state.update { it.copy(lngError = "Kinh độ không hợp lệ") }
            isValid = false
        }

        // Nếu có lỗi -> Ngừng gọi API
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val request = AdminCinemaCreateRequest(
                name = st.name.trim(),
                address = st.address.trim(),
                description = st.description.trim().takeIf { it.isNotEmpty() },
                region = st.region.trim().takeIf { it.isNotEmpty() },
                cineplex = st.cineplex.trim().takeIf { it.isNotEmpty() },
                latitude = latDouble,
                longitude = lngDouble,
                isActive = st.isActive
            )

            val result = repository.createCinema(request, st.logoUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Có lỗi xảy ra, vui lòng thử lại!") }
            }
        }
    }
}