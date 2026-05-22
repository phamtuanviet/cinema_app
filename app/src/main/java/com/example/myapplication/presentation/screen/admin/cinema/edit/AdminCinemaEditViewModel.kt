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
data class AdminCinemaEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Lỗi chung dùng cho Toast

    // Form data & Validation Errors
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

    val currentLogoUrl: String? = null,
    val newLogoUri: Uri? = null,

    val availableRegions: List<String> = emptyList(),
    val availableCineplexes: List<String> = emptyList()
)

// (Sealed class CinemaEditEvent giữ nguyên như cũ)

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
            // 🔥 Tự động XÓA LỖI ĐỎ khi người dùng gõ lại vào ô
            is CinemaEditEvent.NameChanged -> _state.update { it.copy(name = event.name, nameError = null) }
            is CinemaEditEvent.AddressChanged -> _state.update { it.copy(address = event.address, addressError = null) }
            is CinemaEditEvent.LatitudeChanged -> _state.update { it.copy(latitude = event.lat, latError = null) }
            is CinemaEditEvent.LongitudeChanged -> _state.update { it.copy(longitude = event.lng, lngError = null) }

            is CinemaEditEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is CinemaEditEvent.RegionChanged -> _state.update { it.copy(region = event.region) }
            is CinemaEditEvent.CineplexChanged -> _state.update { it.copy(cineplex = event.cineplex) }
            is CinemaEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is CinemaEditEvent.LogoPicked -> _state.update { it.copy(newLogoUri = event.uri) }

            is CinemaEditEvent.GoogleMapsLinkChanged -> {
                _state.update { it.copy(googleMapsLink = event.link) }
                extractCoordinatesFromLink(event.link)
            }
        }
    }

    // 🔥 Hàm dọn dẹp lỗi sau khi Toast đã hiển thị xong
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

    fun updateCinema(context: Context) {
        val st = _state.value
        var isValid = true

        // 1. Kiểm tra Ảnh (Phải có ảnh cũ hoặc ảnh mới)
        if (st.currentLogoUrl == null && st.newLogoUri == null) {
            _state.update { it.copy(error = "Vui lòng cung cấp Logo cho rạp!") }
            return
        }

        // 2. Kiểm tra các ô bắt buộc
        if (st.name.isBlank()) {
            _state.update { it.copy(nameError = "Tên rạp không được để trống") }
            isValid = false
        }
        if (st.address.isBlank()) {
            _state.update { it.copy(addressError = "Địa chỉ rạp không được để trống") }
            isValid = false
        }

        // 3. Kiểm tra tính hợp lệ của Toạ độ (nếu có nhập)
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

        // NẾU CÓ LỖI -> Dừng lại, không gửi API
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminCinemaUpdateRequest(
                name = st.name.trim(),
                address = st.address.trim(),
                description = st.description.trim().takeIf { it.isNotEmpty() },
                region = st.region.trim().takeIf { it.isNotEmpty() },
                cineplex = st.cineplex.trim().takeIf { it.isNotEmpty() },
                latitude = latDouble,
                longitude = lngDouble,
                isActive = st.isActive
            )

            val result = repository.updateCinema(cinemaId, request, st.newLogoUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Cập nhật thất bại") }
            }
        }
    }
}