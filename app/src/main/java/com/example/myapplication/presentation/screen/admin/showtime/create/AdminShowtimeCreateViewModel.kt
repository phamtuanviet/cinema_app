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

// --- STATE ---
data class ShowtimeCreateState(
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    // Phim
    val movieSearchQuery: String = "",
    val movieSuggestions: List<SimpleItemDto> = emptyList(),
    val selectedMovie: SimpleItemDto? = null,

    // Rạp
    val cinemaSearchQuery: String = "",
    val cinemaSuggestions: List<SimpleItemDto> = emptyList(),
    val selectedCinema: SimpleItemDto? = null,

    // Thời gian (Sử dụng String tạm thời định dạng yyyy-MM-dd HH:mm)
    val startTimeStr: String = "",
    val endTimeStr: String = "",

    // Phòng (Tự động load khi có Rạp + StartTime + EndTime)
    val isLoadingRooms: Boolean = false,
    val availableRooms: List<SimpleItemDto> = emptyList(),
    val selectedRoom: SimpleItemDto? = null,

    // Giá
    val basePrice: String = "50000",
    val weekendModifier: String = "0"// VD: Tăng 20% vào cuối tuần
)

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
                _state.update { it.copy(movieSearchQuery = event.query, selectedMovie = null) }
                searchMovies(event.query)
            }
            is ShowtimeCreateEvent.MovieSelected -> {
                _state.update { it.copy(selectedMovie = event.movie, movieSearchQuery = event.movie.name, movieSuggestions = emptyList()) }
            }

            is ShowtimeCreateEvent.CinemaQueryChanged -> {
                _state.update { it.copy(cinemaSearchQuery = event.query, selectedCinema = null, selectedRoom = null, availableRooms = emptyList()) }
                searchCinemas(event.query)
            }
            is ShowtimeCreateEvent.CinemaSelected -> {
                _state.update { it.copy(selectedCinema = event.cinema, cinemaSearchQuery = event.cinema.name, cinemaSuggestions = emptyList()) }
                checkAndFetchRooms() // Chọn rạp xong, thử check xem fetch phòng được chưa
            }

            is ShowtimeCreateEvent.StartTimeChanged -> {
                _state.update { it.copy(startTimeStr = event.time) }
                checkAndFetchRooms()
            }
            is ShowtimeCreateEvent.EndTimeChanged -> {
                _state.update { it.copy(endTimeStr = event.time) }
                checkAndFetchRooms()
            }

            is ShowtimeCreateEvent.RoomSelected -> _state.update { it.copy(selectedRoom = event.room) }
            is ShowtimeCreateEvent.BasePriceChanged -> _state.update { it.copy(basePrice = event.price) }
            is ShowtimeCreateEvent.WeekendModifierChanged -> _state.update { it.copy(weekendModifier = event.modifier) }
        }
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
        if (st.selectedMovie == null || st.selectedRoom == null) {
            _state.update { it.copy(error = "Vui lòng chọn đủ Phim, Rạp và Phòng") }
            return
        }

        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val isoStart = LocalDateTime.parse(st.startTimeStr, formatter).toString()
            val isoEnd = LocalDateTime.parse(st.endTimeStr, formatter).toString()

            viewModelScope.launch {
                _state.update { it.copy(isSaving = true, error = null) }

                val req = AdminShowtimeCreateRequest(
                    movieId = st.selectedMovie.id,
                    roomId = st.selectedRoom.id,
                    startTime = isoStart,
                    endTime = isoEnd,
                    basePrice = st.basePrice.toDoubleOrNull() ?: 50000.0,
                    weekendModifier = st.weekendModifier.toDoubleOrNull() ?: 1.0
                )

                val result = repository.createShowtime(req)
                if (result.isSuccess) {
                    _state.update { it.copy(isSaving = false, isSuccess = true) }
                } else {
                    _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Định dạng thời gian không hợp lệ. Vui lòng nhập: yyyy-MM-dd HH:mm") }
        }
    }
}