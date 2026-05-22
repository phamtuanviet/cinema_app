package com.example.myapplication

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.data.remote.dto.AdminComboDto
import com.example.myapplication.domain.repository.AdminComboRepository
import com.example.myapplication.presentation.screen.admin.combo.edit.AdminComboEditViewModel
import com.example.myapplication.presentation.screen.admin.combo.edit.ComboEditEvent
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
import io.mockk.coVerify

import org.junit.Assert.*
import org.junit.Before


@OptIn(ExperimentalCoroutinesApi::class)
class AdminComboEditViewModelTest {

    private val repository = mockk<AdminComboRepository>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    // Mock SavedStateHandle với ID của combo cần edit
    private val savedStateHandle = SavedStateHandle(mapOf("comboId" to "c_123"))

    private lateinit var viewModel: AdminComboEditViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock dữ liệu ban đầu trả về từ Repository
        val mockCombo = AdminComboDto(
            id = "c_123",
            name = "Combo Bắp Nước",
            description = "Ngon tuyệt",
            price = 50000.0,
            imageUrl = "old_image.jpg",
            isActive = true
        )
        coEvery { repository.getComboById("c_123") } returns Result.success(mockCombo)

        viewModel = AdminComboEditViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads combo data and formats price correctly`() = runTest {
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Combo Bắp Nước", state.name)
        assertEquals("50000", state.priceStr) // Kiểm tra logic định dạng giá
        assertFalse(state.isLoadingData)
    }

    @Test
    fun `saveCombo validation fails when name is blank`() = runTest {
        // Gõ tên thành khoảng trắng
        viewModel.onEvent(ComboEditEvent.NameChanged("   "))

        viewModel.onEvent(ComboEditEvent.SaveClicked(context))
        advanceUntilIdle()

        assertEquals("Tên Combo không được để trống", viewModel.state.value.nameError)
        // Đảm bảo không gọi repository khi dữ liệu sai
        coVerify(exactly = 0) { repository.updateCombo(any(), any(), any(), any()) }
    }

    @Test
    fun `saveCombo success flow`() = runTest {
        // Given: Gõ lại dữ liệu đúng
        viewModel.onEvent(ComboEditEvent.NameChanged("Combo Vip"))
        viewModel.onEvent(ComboEditEvent.PriceChanged("99000"))

        coEvery {
            repository.updateCombo(any(), any(), any(), any())
        } returns Result.success(
            AdminComboDto(
                id = "c_123",
                name = "Combo Bắp Nước",
                description = "Mô tả combo",
                price = 99000.0,
                imageUrl = "image_url.jpg",
                isActive = true
            )
        )

        // When
        viewModel.onEvent(ComboEditEvent.SaveClicked(context))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isSaving)
    }
}