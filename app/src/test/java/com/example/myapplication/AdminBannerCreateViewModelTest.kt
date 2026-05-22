package com.example.myapplication

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.dto.AdminBannerDto
import com.example.myapplication.domain.repository.AdminBannerRepository
import com.example.myapplication.presentation.screen.admin.banner.create.AdminBannerCreateViewModel
import com.example.myapplication.presentation.screen.admin.banner.create.BannerCreateEvent
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
class AdminBannerCreateViewModelTest {


    private val repository = mockk<AdminBannerRepository>()
    private val context = mockk<Context>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AdminBannerCreateViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Giả lập dữ liệu phim khi khởi tạo ViewModel
        coEvery { repository.getActiveMovies() } returns Result.success(emptyList())
        viewModel = AdminBannerCreateViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveBanner should show error if image is missing`() = runTest {
        viewModel.onEvent(BannerCreateEvent.SaveClicked(context))
        advanceUntilIdle()

        assertEquals("Vui lòng chọn ảnh bìa cho Banner", viewModel.state.value.error)
    }

    @Test
    fun `saveBanner with URL type should show error if url is blank`() = runTest {
        // Given: Đã chọn ảnh, chọn type là URL nhưng url trống
        val mockUri = mockk<Uri>()
        viewModel.onEvent(BannerCreateEvent.ImageSelected(mockUri))
        viewModel.onEvent(BannerCreateEvent.ActionTypeChanged("URL"))

        // When
        viewModel.onEvent(BannerCreateEvent.SaveClicked(context))
        advanceUntilIdle()

        // Then
        assertEquals("Vui lòng nhập đường dẫn URL", viewModel.state.value.targetUrlError)
    }

    @Test
    fun `saveBanner success flow`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        viewModel.onEvent(BannerCreateEvent.ImageSelected(mockUri))
        viewModel.onEvent(BannerCreateEvent.ActionTypeChanged("MOVIE"))
        viewModel.onEvent(BannerCreateEvent.MovieSelected("movie_123"))
        val mockBanner = AdminBannerDto(
            id = "1",
            imageUrl = "https://example.com/image.jpg",
            actionType = "MOVIE",
            targetUrl = null,
            movieId = "movie_123",
            movieName = "Inception",
            priority = 1,
            isActive = true
        )

        coEvery {
            repository.createBanner(any(), any(), any())
        } returns Result.success(mockBanner)

        // When
        viewModel.onEvent(BannerCreateEvent.SaveClicked(context))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isSaving)
    }
}