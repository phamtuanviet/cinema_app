package com.example.myapplication

import com.example.myapplication.data.remote.dto.AdminShowtimeDto
import com.example.myapplication.data.remote.dto.SimpleItemDto
import com.example.myapplication.domain.repository.AdminShowtimeRepository
import com.example.myapplication.presentation.screen.admin.showtime.create.AdminShowtimeCreateViewModel
import com.example.myapplication.presentation.screen.admin.showtime.create.ShowtimeCreateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import io.mockk.mockk
import io.mockk.coEvery
import kotlinx.coroutines.test.advanceTimeBy

import org.junit.Assert.*
import org.junit.Before


@OptIn(ExperimentalCoroutinesApi::class)
class AdminShowtimeCreateViewModelTest {

    private val repository = mockk<AdminShowtimeRepository>()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AdminShowtimeCreateViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAvailableRooms(any(), any(), any()) } returns Result.success(emptyList())
        coEvery { repository.searchMovies(any()) } returns Result.success(emptyList())
        coEvery { repository.searchCinemas(any()) } returns Result.success(emptyList())

        viewModel = AdminShowtimeCreateViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchMovies triggers repository after delay`() = runTest {
        // Given
        coEvery { repository.searchMovies("Avatar") } returns Result.success(
            listOf(SimpleItemDto("1", "Avatar"))
        )

        // When
        viewModel.onEvent(ShowtimeCreateEvent.MovieQueryChanged("Avatar"))

        // Advance time to pass the 500ms delay
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.movieSuggestions.size)
        assertEquals("Avatar", viewModel.state.value.movieSuggestions.first().name)
    }

    @Test
    fun `checkAndFetchRooms clears rooms if start time is incomplete`() = runTest {
        // Given
        val cinema = SimpleItemDto("c1", "Cinema 1")
        viewModel.onEvent(ShowtimeCreateEvent.CinemaSelected(cinema))

        // When: Nhập giờ chưa đủ độ dài (length < 16)
        viewModel.onEvent(ShowtimeCreateEvent.StartTimeChanged("2026-05-22"))

        // Then
        assertTrue(viewModel.state.value.availableRooms.isEmpty())
        assertNull(viewModel.state.value.selectedRoom)
    }

    @Test
    fun `createShowtime fails if end time is before start time`() = runTest {
        // Given: Chọn phim, rạp, phòng giả lập
        viewModel.onEvent(ShowtimeCreateEvent.MovieSelected(SimpleItemDto("m1", "Movie")))
        viewModel.onEvent(ShowtimeCreateEvent.CinemaSelected(SimpleItemDto("c1", "Cinema")))

        // Set time: Start > End
        viewModel.onEvent(ShowtimeCreateEvent.StartTimeChanged("2026-05-22 15:00"))
        viewModel.onEvent(ShowtimeCreateEvent.EndTimeChanged("2026-05-22 14:00"))
        viewModel.onEvent(ShowtimeCreateEvent.RoomSelected(SimpleItemDto("r1", "Room 1")))

        // When
        viewModel.createShowtime()

        // Then
        assertEquals("Giờ kết thúc phải sau giờ bắt đầu", viewModel.state.value.endTimeError)
    }

    @Test
    fun `createShowtime success flow`() = runTest {
        // Given
        viewModel.onEvent(ShowtimeCreateEvent.MovieSelected(SimpleItemDto("m1", "Movie")))
        viewModel.onEvent(ShowtimeCreateEvent.CinemaSelected(SimpleItemDto("c1", "Cinema")))
        viewModel.onEvent(ShowtimeCreateEvent.StartTimeChanged("2026-05-22 14:00"))
        viewModel.onEvent(ShowtimeCreateEvent.EndTimeChanged("2026-05-22 16:00"))
        viewModel.onEvent(ShowtimeCreateEvent.RoomSelected(SimpleItemDto("r1", "Room 1")))

        val mockShowtime = AdminShowtimeDto(
            id = "st_123",
            movieName = "Avatar",
            moviePosterUrl = null,
            cinemaName = "Beta Mỹ Đình",
            roomName = "Room 1",
            startTime = "2026-05-22T14:00:00",
            endTime = "2026-05-22T16:00:00",
            basePrice = 50000.0,
            status = "ACTIVE"
        )

        coEvery { repository.createShowtime(any()) } returns Result.success(mockShowtime)

        // When
        viewModel.createShowtime()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isSaving)
    }
}