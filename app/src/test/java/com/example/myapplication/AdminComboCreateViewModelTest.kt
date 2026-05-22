package com.example.myapplication

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.data.remote.dto.AdminBannerDto
import com.example.myapplication.data.remote.dto.AdminComboDto
import com.example.myapplication.domain.repository.AdminBannerRepository
import com.example.myapplication.domain.repository.AdminComboRepository
import com.example.myapplication.presentation.screen.admin.banner.create.AdminBannerCreateViewModel
import com.example.myapplication.presentation.screen.admin.banner.create.BannerCreateEvent
import com.example.myapplication.presentation.screen.admin.banner.edit.AdminBannerEditViewModel
import com.example.myapplication.presentation.screen.admin.banner.edit.BannerEditEvent
import com.example.myapplication.presentation.screen.admin.combo.create.AdminComboCreateViewModel
import com.example.myapplication.presentation.screen.admin.combo.create.ComboCreateEvent
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
class AdminComboCreateViewModelTest {

    private val repository = mockk<AdminComboRepository>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AdminComboCreateViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AdminComboCreateViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveCombo shows error if image is missing`() = runTest {
        viewModel.onEvent(ComboCreateEvent.SaveClicked(context))
        advanceUntilIdle()

        assertEquals("Vui lòng chọn ảnh minh họa cho Combo!", viewModel.state.value.error)
    }

    @Test
    fun `saveCombo shows error if name is blank`() = runTest {
        // Given: Đã chọn ảnh, để trống tên
        val mockUri = mockk<Uri>()
        viewModel.onEvent(ComboCreateEvent.ImageSelected(mockUri))
        viewModel.onEvent(ComboCreateEvent.PriceChanged("50000"))

        // When
        viewModel.onEvent(ComboCreateEvent.SaveClicked(context))
        advanceUntilIdle()

        // Then
        assertEquals("Tên Combo không được để trống", viewModel.state.value.nameError)
    }

    @Test
    fun `saveCombo shows error if price is invalid`() = runTest {
        // Given: Đã chọn ảnh, tên hợp lệ, giá âm
        val mockUri = mockk<Uri>()
        viewModel.onEvent(ComboCreateEvent.ImageSelected(mockUri))
        viewModel.onEvent(ComboCreateEvent.NameChanged("Combo Bắp Nước"))
        viewModel.onEvent(ComboCreateEvent.PriceChanged("-1000"))

        // When
        viewModel.onEvent(ComboCreateEvent.SaveClicked(context))
        advanceUntilIdle()

        // Then
        assertEquals("Giá bán không hợp lệ", viewModel.state.value.priceError)
    }

    @Test
    fun `saveCombo success flow`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        viewModel.onEvent(ComboCreateEvent.ImageSelected(mockUri))
        viewModel.onEvent(ComboCreateEvent.NameChanged("Combo Vip"))
        viewModel.onEvent(ComboCreateEvent.PriceChanged("100000"))

        coEvery {
            repository.createCombo(any(), any(), any())
        } returns Result.success(mockk<AdminComboDto>())

        // When
        viewModel.onEvent(ComboCreateEvent.SaveClicked(context))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isSaving)
    }
}