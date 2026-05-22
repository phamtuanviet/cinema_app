package com.example.myapplication.presentation.screen.admin.showtime.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminShowtimeCreateRequest
import com.example.myapplication.data.remote.dto.SimpleItemDto
import com.example.myapplication.domain.repository.AdminShowtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


// --- EVENT ---
sealed class ShowtimeCreateEvent {
    data class MovieQueryChanged(val query: String) : ShowtimeCreateEvent()
    data class MovieSelected(val movie: SimpleItemDto) : ShowtimeCreateEvent()

    data class CinemaQueryChanged(val query: String) : ShowtimeCreateEvent()
    data class CinemaSelected(val cinema: SimpleItemDto) : ShowtimeCreateEvent()

    data class StartTimeChanged(val time: String) : ShowtimeCreateEvent()
    data class EndTimeChanged(val time: String) : ShowtimeCreateEvent()

    data class RoomSelected(val room: SimpleItemDto) : ShowtimeCreateEvent()

    data class BasePriceChanged(val price: String) : ShowtimeCreateEvent()
    data class WeekendModifierChanged(val modifier: String) : ShowtimeCreateEvent()
}

data class ShowtimeCreateState(
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Lỗi chung cho Toast

    // Phim
    val movieSearchQuery: String = "",
    val movieError: String? = null,
    val movieSuggestions: List<SimpleItemDto> = emptyList(),
    val selectedMovie: SimpleItemDto? = null,

    // Rạp
    val cinemaSearchQuery: String = "",
    val cinemaError: String? = null,
    val cinemaSuggestions: List<SimpleItemDto> = emptyList(),
    val selectedCinema: SimpleItemDto? = null,

    // Thời gian
    val startTimeStr: String = "",
    val startTimeError: String? = null,
    val endTimeStr: String = "",
    val endTimeError: String? = null,

    // Phòng
    val isLoadingRooms: Boolean = false,
    val availableRooms: List<SimpleItemDto> = emptyList(),
    val selectedRoom: SimpleItemDto? = null,
    val roomError: String? = null,

    // Giá
    val basePrice: String = "50000",
    val priceError: String? = null,
    val weekendModifier: String = "0"
)



// --- VIEWMODEL ---
@HiltViewModel
class AdminShowtimeCreateViewModel @Inject constructor(
    private val repository: AdminShowtimeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ShowtimeCreateState())
    val state = _state.asStateFlow()

    private var searchMovieJob: Job? = null
    private var searchCinemaJob: Job? = null

    fun onEvent(event: ShowtimeCreateEvent) {
        when (event) {
            is ShowtimeCreateEvent.MovieQueryChanged -> {
                _state.update { it.copy(movieSearchQuery = event.query, selectedMovie = null, movieError = null) }
                searchMovies(event.query)
            }
            is ShowtimeCreateEvent.MovieSelected -> {
                _state.update { it.copy(selectedMovie = event.movie, movieSearchQuery = event.movie.name, movieSuggestions = emptyList(), movieError = null) }
            }

            is ShowtimeCreateEvent.CinemaQueryChanged -> {
                _state.update { it.copy(cinemaSearchQuery = event.query, selectedCinema = null, selectedRoom = null, availableRooms = emptyList(), cinemaError = null) }
                searchCinemas(event.query)
            }
            is ShowtimeCreateEvent.CinemaSelected -> {
                _state.update { it.copy(selectedCinema = event.cinema, cinemaSearchQuery = event.cinema.name, cinemaSuggestions = emptyList(), cinemaError = null) }
                checkAndFetchRooms()
            }

            is ShowtimeCreateEvent.StartTimeChanged -> {
                _state.update { it.copy(startTimeStr = event.time, startTimeError = null) }
                checkAndFetchRooms()
            }
            is ShowtimeCreateEvent.EndTimeChanged -> {
                _state.update { it.copy(endTimeStr = event.time, endTimeError = null) }
                checkAndFetchRooms()
            }

            is ShowtimeCreateEvent.RoomSelected -> _state.update { it.copy(selectedRoom = event.room, roomError = null) }

            is ShowtimeCreateEvent.BasePriceChanged -> _state.update { it.copy(basePrice = event.price, priceError = null) }
            is ShowtimeCreateEvent.WeekendModifierChanged -> _state.update { it.copy(weekendModifier = event.modifier) }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun searchMovies(query: String) {
        searchMovieJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(movieSuggestions = emptyList()) }
            return
        }
        searchMovieJob = viewModelScope.launch {
            delay(500) // Tránh spam API
            val result = repository.searchMovies(query)
            if (result.isSuccess) {
                _state.update { it.copy(movieSuggestions = result.getOrNull() ?: emptyList()) }
            }
        }
    }

    private fun searchCinemas(query: String) {
        searchCinemaJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(cinemaSuggestions = emptyList()) }
            return
        }
        searchCinemaJob = viewModelScope.launch {
            delay(500)
            val result = repository.searchCinemas(query)
            if (result.isSuccess) {
                _state.update { it.copy(cinemaSuggestions = result.getOrNull() ?: emptyList()) }
            }
        }
    }

    // 🔥 LOGIC CỐT LÕI: Tải phòng trống
    private fun checkAndFetchRooms() {
        val currentState = _state.value
        val cinemaId = currentState.selectedCinema?.id
        val start = currentState.startTimeStr
        val end = currentState.endTimeStr

        // Nếu người dùng chưa chọn Rạp, hoặc chưa nhập đủ giờ -> Xóa list phòng
        if (cinemaId == null || start.length < 16 || end.length < 16) {
            _state.update { it.copy(availableRooms = emptyList(), selectedRoom = null) }
            return
        }

        // Parse giờ để gửi định dạng ISO xuống Backend
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val sTime = LocalDateTime.parse(start, formatter).toString() // -> yyyy-MM-ddTHH:mm
            val eTime = LocalDateTime.parse(end, formatter).toString()

            viewModelScope.launch {
                _state.update { it.copy(isLoadingRooms = true, error = null) }
                val result = repository.getAvailableRooms(cinemaId, sTime, eTime)

                if (result.isSuccess) {
                    val rooms = result.getOrNull() ?: emptyList()
                    _state.update {
                        it.copy(
                            isLoadingRooms = false,
                            availableRooms = rooms,
                            // Xóa phòng đã chọn nếu nó không còn trong danh sách phòng trống
                            selectedRoom = if (rooms.contains(it.selectedRoom)) it.selectedRoom else null
                        )
                    }
                } else {
                    _state.update { it.copy(isLoadingRooms = false, error = "Lỗi tải danh sách phòng") }
                }
            }
        } catch (e: Exception) {
            // Lỗi parse thời gian (người dùng đang gõ dở) -> Bỏ qua
        }
    }

    fun createShowtime() {
        val st = _state.value
        var isValid = true

        // 1. Validate Phim & Rạp
        if (st.selectedMovie == null) {
            _state.update { it.copy(movieError = "Vui lòng chọn phim từ danh sách gợi ý") }
            isValid = false
        }
        if (st.selectedCinema == null) {
            _state.update { it.copy(cinemaError = "Vui lòng chọn rạp từ danh sách gợi ý") }
            isValid = false
        }

        // 2. Validate Thời gian
        if (st.startTimeStr.isBlank()) {
            _state.update { it.copy(startTimeError = "Vui lòng chọn giờ bắt đầu") }
            isValid = false
        }
        if (st.endTimeStr.isBlank()) {
            _state.update { it.copy(endTimeError = "Vui lòng chọn giờ kết thúc") }
            isValid = false
        }

        // 3. Logic check: Thời gian kết thúc phải > Thời gian bắt đầu
        var isoStart = ""
        var isoEnd = ""
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

        // 4. Validate Phòng chiếu
        if (st.selectedRoom == null) {
            _state.update { it.copy(roomError = "Vui lòng chọn phòng chiếu") }
            isValid = false
        }

        // 5. Validate Giá vé
        val price = st.basePrice.toDoubleOrNull()
        if (price == null || price < 0) {
            _state.update { it.copy(priceError = "Giá vé không hợp lệ") }
            isValid = false
        }

        // Nếu có lỗi thì DỪNG
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val req = AdminShowtimeCreateRequest(
                movieId = st.selectedMovie!!.id,
                roomId = st.selectedRoom!!.id,
                startTime = isoStart,
                endTime = isoEnd,
                basePrice = price!!,
                weekendModifier = st.weekendModifier.toDoubleOrNull() ?: 0.0
            )

            val result = repository.createShowtime(req)
            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Có lỗi xảy ra khi tạo lịch chiếu") }
            }
        }
    }
}