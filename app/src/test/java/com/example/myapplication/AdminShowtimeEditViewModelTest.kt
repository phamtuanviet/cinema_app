package com.example.myapplication

import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.data.remote.dto.AdminShowtimeDto
import com.example.myapplication.domain.repository.AdminShowtimeRepository
import com.example.myapplication.presentation.screen.admin.showtime.edit.AdminShowtimeEditViewModel
import com.example.myapplication.presentation.screen.admin.showtime.edit.ShowtimeEditEvent
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
class AdminShowtimeEditViewModelTest {

    private val repository = mockk<AdminShowtimeRepository>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val savedStateHandle = SavedStateHandle(mapOf("showtimeId" to "st_123"))

    private lateinit var viewModel: AdminShowtimeEditViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock dữ liệu ban đầu
        val mockShowtime = AdminShowtimeDto(
            id = "st_123", movieName = "Inception", moviePosterUrl = null,
            cinemaName = "Beta", roomName = "Room 1",
            startTime = "2026-05-22T14:00:00", endTime = "2026-05-22T16:00:00",
            basePrice = 50000.0, status = "ACTIVE"
        )
        coEvery { repository.getShowtimeById("st_123") } returns Result.success(mockShowtime)

        viewModel = AdminShowtimeEditViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadShowtimeData parses date correctly for UI`() = runTest {
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("2026-05-22 14:00", state.startTimeStr)
        assertEquals("2026-05-22 16:00", state.endTimeStr)
        assertFalse(state.isLoadingData)
    }

    @Test
    fun `SaveTimeChanges fails when endTime is before startTime`() = runTest {
        // Given
        viewModel.onEvent(ShowtimeEditEvent.StartTimeChanged("2026-05-22 15:00"))
        viewModel.onEvent(ShowtimeEditEvent.EndTimeChanged("2026-05-22 14:00"))

        // When
        viewModel.onEvent(ShowtimeEditEvent.SaveTimeChanges)

        // Then
        assertEquals("Giờ kết thúc phải sau giờ bắt đầu", viewModel.state.value.endTimeError)
    }

    @Test
    fun `updateShowtimeToServer handles conflict error message`() = runTest {
        // Given
        viewModel.onEvent(ShowtimeEditEvent.StartTimeChanged("2026-05-22 14:00"))
        viewModel.onEvent(ShowtimeEditEvent.EndTimeChanged("2026-05-22 16:00"))

        // Giả lập lỗi conflict từ server
        coEvery { repository.updateShowtime(any(), any()) } returns
                Result.failure(Exception("Schedule overlap conflict"))

        // When
        viewModel.onEvent(ShowtimeEditEvent.SaveTimeChanges)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.error!!.contains("trùng giờ"))
    }
}