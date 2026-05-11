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
    val error: String? = null,

    // Thông tin chỉ đọc (Để Admin nhìn cho rõ)
    val movieName: String = "",
    val cinemaRoomInfo: String = "",
    val currentStatus: String = "ACTIVE",

    // Thông tin cho phép sửa
    val startTimeStr: String = "",
    val endTimeStr: String = "",

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

                // Parse thời gian từ chuẩn ISO về định dạng hiển thị cho Picker (yyyy-MM-dd HH:mm)
                val formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val formatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

                val startParsed = LocalDateTime.parse(showtime.startTime, formatterIn).format(formatterOut)
                val endParsed = LocalDateTime.parse(showtime.endTime, formatterIn).format(formatterOut)

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
            is ShowtimeEditEvent.StartTimeChanged -> _state.update { it.copy(startTimeStr = event.time) }
            is ShowtimeEditEvent.EndTimeChanged -> _state.update { it.copy(endTimeStr = event.time) }

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

    private fun updateShowtimeToServer(newStatus: String) {
        val currentState = _state.value

        try {
            // Parse ngược lại từ UI (yyyy-MM-dd HH:mm) sang ISO (yyyy-MM-ddTHH:mm:ss) để gửi Backend
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val isoStart = LocalDateTime.parse(currentState.startTimeStr, formatter).toString()
            val isoEnd = LocalDateTime.parse(currentState.endTimeStr, formatter).toString()

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
                    _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Định dạng thời gian không hợp lệ!") }
        }
    }
}