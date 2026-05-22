package com.example.myapplication.presentation.screen.admin.showtime.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminShowtimeUpdateRequest
import com.example.myapplication.domain.repository.AdminShowtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// --- STATE ---
data class AdminShowtimeEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Lỗi chung dùng để hiển thị Toast

    // Thông tin chỉ đọc
    val movieName: String = "",
    val cinemaRoomInfo: String = "",
    val currentStatus: String = "ACTIVE",

    // Thông tin cho phép sửa (Kèm biến báo lỗi)
    val startTimeStr: String = "",
    val startTimeError: String? = null,

    val endTimeStr: String = "",
    val endTimeError: String? = null,

    // Trạng thái bật/tắt Dialog cảnh báo hủy
    val showCancelDialog: Boolean = false
)
// --- EVENT ---
sealed class ShowtimeEditEvent {
    data class StartTimeChanged(val time: String) : ShowtimeEditEvent()
    data class EndTimeChanged(val time: String) : ShowtimeEditEvent()
    object ToggleCancelDialog : ShowtimeEditEvent() // Mở/đóng Dialog xác nhận hủy
    object ConfirmCancelShowtime : ShowtimeEditEvent() // Đồng ý hủy
    object SaveTimeChanges : ShowtimeEditEvent() // Lưu thay đổi thời gian
}

// --- VIEWMODEL ---
@HiltViewModel
class AdminShowtimeEditViewModel @Inject constructor(
    private val repository: AdminShowtimeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val showtimeId: String = checkNotNull(savedStateHandle["showtimeId"])

    private val _state = MutableStateFlow(AdminShowtimeEditState())
    val state = _state.asStateFlow()

    init {
        loadShowtimeData()
    }

    private fun loadShowtimeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true, error = null) }
            val result = repository.getShowtimeById(showtimeId)

            if (result.isSuccess) {
                val showtime = result.getOrNull()!!

                // Parse thời gian từ chuẩn ISO về định dạng hiển thị cho Picker
                val formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val formatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

                val startParsed = try { LocalDateTime.parse(showtime.startTime, formatterIn).format(formatterOut) } catch (e: Exception) { showtime.startTime }
                val endParsed = try { LocalDateTime.parse(showtime.endTime, formatterIn).format(formatterOut) } catch (e: Exception) { showtime.endTime }

                _state.update {
                    it.copy(
                        isLoadingData = false,
                        movieName = showtime.movieName,
                        cinemaRoomInfo = "${showtime.cinemaName} - ${showtime.roomName}",
                        currentStatus = showtime.status,
                        startTimeStr = startParsed,
                        endTimeStr = endParsed
                    )
                }
            } else {
                _state.update { it.copy(isLoadingData = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun onEvent(event: ShowtimeEditEvent) {
        when (event) {
            // 🔥 Xóa lỗi đỏ khi Admin chọn lại giờ mới
            is ShowtimeEditEvent.StartTimeChanged -> _state.update { it.copy(startTimeStr = event.time, startTimeError = null) }
            is ShowtimeEditEvent.EndTimeChanged -> _state.update { it.copy(endTimeStr = event.time, endTimeError = null) }

            is ShowtimeEditEvent.ToggleCancelDialog -> {
                _state.update { it.copy(showCancelDialog = !it.showCancelDialog) }
            }

            is ShowtimeEditEvent.ConfirmCancelShowtime -> {
                _state.update { it.copy(showCancelDialog = false) }
                updateShowtimeToServer(newStatus = "CANCELED")
            }

            is ShowtimeEditEvent.SaveTimeChanges -> {
                updateShowtimeToServer(newStatus = "ACTIVE")
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun updateShowtimeToServer(newStatus: String) {
        val st = _state.value
        var isValid = true
        var isoStart = ""
        var isoEnd = ""

        // 1. Kiểm tra không được để trống
        if (st.startTimeStr.isBlank()) {
            _state.update { it.copy(startTimeError = "Vui lòng chọn giờ bắt đầu") }
            isValid = false
        }
        if (st.endTimeStr.isBlank()) {
            _state.update { it.copy(endTimeError = "Vui lòng chọn giờ kết thúc") }
            isValid = false
        }

        // 2. Kiểm tra Logic thời gian (Kết thúc phải lớn hơn Bắt đầu)
        if (st.startTimeStr.isNotBlank() && st.endTimeStr.isNotBlank()) {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val startDt = LocalDateTime.parse(st.startTimeStr, formatter)
                val endDt = LocalDateTime.parse(st.endTimeStr, formatter)

                if (endDt.isBefore(startDt) || endDt.isEqual(startDt)) {
                    _state.update { it.copy(endTimeError = "Giờ kết thúc phải sau giờ bắt đầu") }
                    isValid = false
                } else {
                    isoStart = startDt.toString()
                    isoEnd = endDt.toString()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Định dạng thời gian bị lỗi. Vui lòng thử lại.") }
                isValid = false
            }
        }

        // Dừng gọi API nếu có lỗi
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminShowtimeUpdateRequest(
                startTime = isoStart,
                endTime = isoEnd,
                status = newStatus
            )

            val result = repository.updateShowtime(showtimeId, request)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                val errorMsg = result.exceptionOrNull()?.message?.lowercase() ?: ""
                // Dịch lỗi nếu do server báo trùng lịch phòng
                val friendlyError = if (errorMsg.contains("conflict") || errorMsg.contains("overlap")) {
                    "Lịch chiếu này bị trùng giờ với một phim khác trong cùng phòng!"
                } else {
                    result.exceptionOrNull()?.message ?: "Có lỗi xảy ra, vui lòng thử lại!"
                }

                _state.update { it.copy(isSaving = false, error = friendlyError) }
            }
        }
    }
}