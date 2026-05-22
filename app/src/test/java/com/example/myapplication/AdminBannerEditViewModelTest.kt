package com.example.myapplication

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.data.remote.dto.AdminBannerDto
import com.example.myapplication.domain.repository.AdminBannerRepository
import com.example.myapplication.presentation.screen.admin.banner.create.AdminBannerCreateViewModel
import com.example.myapplication.presentation.screen.admin.banner.create.BannerCreateEvent
import com.example.myapplication.presentation.screen.admin.banner.edit.AdminBannerEditViewModel
import com.example.myapplication.presentation.screen.admin.banner.edit.BannerEditEvent
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

import org.junit.Assert.*
import org.junit.Before


@OptIn(ExperimentalCoroutinesApi::class)
class AdminBannerEditViewModelTest {

    private val repository = mockk<AdminBannerRepository>()
    private val context = mockk<Context>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    // Giả lập SavedStateHandle
    private val savedStateHandle = SavedStateHandle(mapOf("bannerId" to "banner_123"))

    private lateinit var viewModel: AdminBannerEditViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock dữ liệu trả về cho init
        val mockBanner = AdminBannerDto(
            id = "banner_123", imageUrl = "old_url.jpg", actionType = "MOVIE",
            targetUrl = null, movieId = "m1", movieName = "Movie 1", priority = 1, isActive = true
        )
        coEvery { repository.getBannerById("banner_123") } returns Result.success(mockBanner)
        coEvery { repository.getActiveMovies() } returns Result.success(emptyList())

        viewModel = AdminBannerEditViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads banner data correctly`() = runTest {
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("MOVIE", state.actionType)
        assertEquals("m1", state.selectedMovieId)
        assertEquals("old_url.jpg", state.existingImageUrl)
        assertFalse(state.isLoadingData)
    }

    @Test
    fun `saveBanner success flow`() = runTest {
        // Given
        coEvery {
            repository.updateBanner(any(), any(), any(), any())
        } returns Result.success(mockk<AdminBannerDto>())

        // When
        viewModel.onEvent(BannerEditEvent.SaveClicked(context))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `error handling when update fails`() = runTest {
        // Given
        val errorMessage = "Lỗi server"
        coEvery {
            repository.updateBanner(any(), any(), any(), any())
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.onEvent(BannerEditEvent.SaveClicked(context))
        advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSaving)
    }
}